/**
 * Deals with getting, parsing, and displaying sonnet details.
 * @type {jQuery}
 * @author Josh Harkema
 * @date 28 June 2018
 */

var pageSize = $("#pageSize").val();
var orderBy = $("#orderBy").val();
var orderDirection = $("#orderDirection").val();
var currentPage = 1;

// AJAX returns async call to get all sonnets.
function getSonnets(callback) {
    $.ajax({
        'url': "/sonnets/all/paged?" + "page=" + currentPage + "&size=" + pageSize + "&sort="
        + orderBy + "&" + orderBy + ".dir=" + orderDirection,
        'method': 'GET',
        'dataType': 'json',
        success: function (response) {
            callback(response);
        }
    });
}

// Handles async to keep everything working.
getSonnets(function (result) {
    for (var key in result.content) {
        var obj = result.content[key];
        createCard(obj);
    }
});

// Designs and adds a card to the container div.
function createCard(obj) {
    var div = document.getElementById('card-container');
    var text = "";

    for (var line in obj['text']) {
        text += '\n<li>' + obj['text'][line] + '</li>';
    }

    var html =
        '<div class="card border-light mb-2" style="max-width: 24rem; min-width: 23rem;">' +
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
