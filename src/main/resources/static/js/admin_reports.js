/**
 * Handles getting the report data and generating the reports.
 * @type {jQuery}
 * @author Josh Harkema
 * @date 29 June 2018
 */

var username = $('#username').val();

// Generate reports when the "Get Report" button is pressed.
$('#reportButton').click(function () {
    getReport(function (result) {
        var w = window.open();
        var text = "";
        var totalRecords = Object.keys(result).length;
        text += "<html>";
        text += "\n<body>";
        text += "\n<h1>Report for: " + username + "</h1>";
        text += "\n<h2>Total Records for User: " + totalRecords + "</h2>";

        for (var key in result) {
            var obj = result[key];
            var date = moment(obj['updatedAt']).format("MM-DD-YYYY HH:mm");
            text += '\n<p><strong>Author:</strong> ' + obj['firstName'] + ' ' + obj["lastName"] + ' | ' +
                '<strong>Title:</strong> ' + obj["title"] + ' | ' +
                '<strong>Date Added:</strong> ' + date + '</p>';
        }

        text += "\n</body>";
        text += "</html>";

        w.document.open().write(text);
    })
});

// Get the reports from the AJAX reports endpoint.
function getReport(callback) {
    username = $('#username').val();
    var after = $('#after').val();
    var before = $('#before').val();

    var data = {'username': username, 'after': after, 'before': before};
    $.ajax({
        url: '/admin/reports/create',
        type: 'post',
        data: JSON.stringify(data),
        headers: {
            "Content-Type": "application/json"
        },
        success: function (response) {
            callback(response);
        }
    })
}

