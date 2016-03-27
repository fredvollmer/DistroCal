var days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
var nodes;
var modalActive = false;

$(document).ready(function () {
    // Setup tables
    var tables = $(".calendarTable");

    // for each table
    $(tables).each(function (index, table) {
        // Add row for each half hour
        for (var i = 0; i < 47; i++) {
            var row = table.insertRow(i);

            // Insert 7 cells
            for (var j = 0; j < 7; j++) {
                var cell = row.insertCell(j);
                if (i == 0) {
                    cell.innerHTML = days[j];
                }
            }
        }
    });

    // Create nodes
    nodes = [
        new Node("127.0.0.1:8000", "127.0.0.1:1024", 0, "#e13d67"),
        new Node("127.0.0.1:8001", "127.0.0.1:1025", 1, "#3bace1")
    ];

    // Cancel click
    $("#cancel").click(function (e) {
        var modal = $(".eventModal");
        modal.animate({
            "opacity": 0
        }, 400, function () {
            modal.css("display", "none");
            modalActive = false;
        });
    });

    // Crash click
    

    // Event creation clicks on table
    $(".calendarTable").click(function(e) {
        if (modalActive) return;
        // Get node
        var nodeIndex = $(this).attr('id').charAt($(this).attr('id').length - 1);

        // Get x and y in %
        var x = ((e.pageX - $(this).offset().left) / $(this).width()) * 100;
        var y = ((e.pageY - $(this).offset().top) / $(this).height()) * 100;

        // Get day and start
        var day = Math.floor(x / 14.28);
        var start = Math.floor(y / 2);

        showNewEventDialogue(nodes[nodeIndex], day, start);

    });

});

function Node (address, uid, cell, color) {
    this.initialized = false;
    this.events = [];
    this.uid = uid;
    this.address = address;
    this.color = color;

    this.fetchAppointments = function (initial) {
        $.ajax({
            method: "GET",
            url: "http://" + address,
            context: this,
            cache: false,
            dataType: "json",
            error: function (e) {
                //alert("Request at " + address + " failed: " + e.message);
                $("#_" + cell).append('<i class="fa fa-exclamation-triangle"></i>');
            },
            statusCode: {
                200: function (data) {
                    this.processEvents(data);
                },
                500: function (e) {
                    //alert(e.message);
                    $("#_" + cell).append('<i class="fa fa-exclamation-triangle"></i>');
                }
            },
            complete: function () {
                if (initial) {
                    $("#_" + cell + " .loader").css("opacity", 0);
                    setTimeout(function () {
                        $("#_" + cell + " .loader").css("display", "none");
                    }, 1000);
                }
            }
        });
    };

    this.placeEvent = function (event) {
        // Compute x, y and height coordinates (as %)
        // In terms of upper left corner
        var x = event.day * 14.28;
        var y = event.startTime * 2.0 + 2;
        var height = (event.endTime - event.startTime) * 2.08333;

        // Determine bg color based on pending status
        var bg = (event.pending) ? "lightgray" : this.color;

        // Build event block HTML
        var html = "<div class='event' style='background-color: " + bg + "; "
                + "top: " + y + "%; " + "left: " + x + "%; " + "height: " + height + "%;' "
            + "id='event_" + event.day + "-" + event.startTime + "-" + this.uid + "'>"
            + "<span class='event-title'>" + event.name + "</span>"
            + "<span class='event-nodes'>";

        $(event.nodes).each(function (index, node) {
            // Get color based on address
            var color = "#fff";
            $(nodes).each(function (index, n) {
                if (node == n.uid) {
                    color = n.color;
                    return false;
                }
            });
            html += "<div class='event-color-block' style='background-color: "
                + color + ";'></div>"
        });

        html += "</span></div>";

        // Append html
        $("#_" + cell + " .calendarTable").append(html);
    };

    this.processEvents = function (data) {
        var events = data.events;
        var status = data.status;

        // Remove all event block; yes it's terribly inefficient to build from scratch but a better solution
        // isn't needed for this example
        $("#_" + cell + " .event").remove();

        // Set title bar color based on status
        var titleColor = (status == 1) ? "#61ae6e" : "#e16b66";
        $("#_" + cell + " .cellTitle").css("background-color", titleColor);

        // Process each event and add to calendar
        var node = this;
        $(events).each(function (index, event) {
            node.placeEvent(event);
        });
    };

    // Constructor
    // Set title
    $("#title_" + cell).append(address);

    // Set color
    $(("#title_" + cell)).css("border-color", color);

    // Initial GET
    this.fetchAppointments(true);

};

function postEvent (event, node) {
    $.ajax({
        method: "POST",
        url: "http://" + node.address,
        context: this,
        cache: false,
        dataType: "json",
        data: JSON.stringify(event),
        contentType: "application/json",
        synchronous: true,
        error: function (e) {
            alert("An error occurred with creating new event.");
        },
        statusCode: {
            201: function (data) {
                node.fetchAppointments();
            },
            500: function (e) {
                alert("An unknown server error just done happened.");
            },
            505: function (e) {
                alert("One of the nodes participating in your event is busy during that time!");
            }
        },
        complete: function () {
        }
    });
}

function showNewEventDialogue (node, day, start) {
    modalActive = true;
    var modal = $(".eventModal");
    var time = days[day] + " @ " + ("00" + Math.floor(start / 2)).slice(-2) + ":" + ("00" + ((start % 2) * 30)).slice(-2);
    modal.find(".modal-title").html(time);
    var nodeChecks = "";
    $(this).html('Create');
    $(nodes).each(function(index, n) {
        if (node.address != n.address) {
            nodeChecks += '<input type="checkbox" name="node" value="' + n.uid + '">' + n.address + '<br>';
        }
    });
    $(modal.find(".nodeSelector")).html(nodeChecks);

    // Bind submit click
    $("#submit").click (function () {
        $(this).html('<i class="fa fa-cog fa-spin"></i>');
        var event = {};
        event.name = $("#eventName").value;
        event.startTime = start;
        event.endTime = (start + (2 * $("#eventLength").value));
        event.nodes = [];
        $(".nodeSelector input:checked").each (function (index, box) {
            event.nodes.push($(box).value);
        });

        postEvent(event, node);

        modal.animate({
            "opacity": 0
        }, 400, function () {
            modal.css("display", "none");
            modalActive = false;
        });
    });

    // show modal
    modal.css("display", "block");
    modal.animate({
        "opacity": 1.0
    }, 400);
}