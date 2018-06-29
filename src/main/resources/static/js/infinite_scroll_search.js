/**
 * Deals with getting, parsing, and displaying sonnet details.
 * @type {jQuery}
 * @author Josh Harkema
 * @date 29 June 2018
 */
var firstName = $("#firstName").val();
var lastName = $("#lastName").val();
var title = $("#title").val();
var period = $("#period").val();
var text = $("#text").val();

// Execute search when button is pressed. Replace all spaces with "_"'s
$("#searchButton").click(function () {
    $("#card-container").empty(); // Empty the div each time the button is pressed.
    firstName = $("#firstName").val().replace(" ", "_");
    lastName = $("#lastName").val().replace(" ", "_");
    title = $("#title").val().replace(" ", "_");
    period = $("#period").val().replace(" ", "_");
    text = $("#text").val().replace(" ", "_");

    getSonnets(function (result) {
        for (var key in result) {
            var obj = result[key];
            createCard(obj);
        }
    })
});

// Gets sonnets via a GET call. All search params are passed as URL params.
function getSonnets(callback) {
    $.ajax({
        url: '/sonnets/search?' + 'firstName=' + firstName + '&lastName=' + lastName + '&title=' + title +
        '&period=' + period + '&text=' + text,
        method: 'GET',
        headers: {
            "Content-Type": "application/json"
        },
        dataType: 'json',
        success: function (response) {
            if (Object.keys(response).length === 0) {
                var div = document.getElementById('card-container');
                div.innerHTML += "<p class='lead mt-4' style='text-align: center'>No Results</p>"
            }
            callback(response);
        }
    });
}

// Designs and adds a card to the container div.
function createCard(obj) {
    var div = document.getElementById('card-container');
    var text = "";

    for (var line in obj['text']) {
        text += '\n<li>' + obj['text'][line] + '</li>';
    }

    var html =
        '<div class="card border-light mb-2" style="max-width: 24rem; min-width: 24rem;">' +
        '\n<h5 class="card-header">' + obj['title'] + '</h5>' +
        '\n<div class="card-body">' +
        '\n <h5 class="card-title">' + obj['firstName'] + ' ' + obj['lastName'] + '</h5>' +
        '\n <ul class="list-unstyled">' +
        text +
        '\n </ul>' +
        '\n</div>' +
        '\n <ul class="list-group list-group-flush">' +
        '\n  <li class="list-group-item"><strong>Year of publication:</strong> ' + obj['publicationYear'] + '</li>' +
        '\n  <li class="list-group-item"><strong>Period of publication:</strong> ' + obj['period'] + '</li>' +
        '\n  <li class="list-group-item"><strong>Source:</strong> ' + obj['sourceDesc'] + '</li>' +
        '\n  <li class="list-group-item"><strong>Rights:</strong> ' + obj['publicationStmt'] + '</li>' +
        '\n  <li class="list-group-item">' +
        '\n   <a href="/sonnets/xml/by_id/' + obj['id'] + '" target="_blank" class="card-link">XML</a>' +
        '\n   <a href="/sonnets/by_id/' + obj['id'] + '" target="_blank" class="card-link">JSON</a>' +
        '\n   <a href="/sonnets/tei/by_id/' + obj['id'] + '" target="_blank" class="card-link">TEI</a>' +
        '\n   <a href="/sonnets/txt/by_id/' + obj['id'] + '" target="_blank" class="card-link">TXT</a>' +
        '\n   </li>' +
        '\n  </ul>' +
        '\n</div>';

    div.innerHTML += html;
}