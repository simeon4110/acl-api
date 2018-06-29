/**
 * Handles AJAX calls to add a new user to the database.
 * @author Josh Harkema
 * @date 28 June 2018
 */
function addUser() {
    var username = $("#username").val();
    var email = $("#email").val();
    var password = $("#password").val();
    var password1 = $("#password1").val();
    var admin = $("#isAdmin").val();

    var data = {"username": username, "email": email, "password": password, "password1": password1, "admin": admin};

    $.ajax({
        url: '/admin/user/add',
        type: 'post',
        data: JSON.stringify(data),
        headers: {
            "Content-Type": "application/json"
        },
        dataType: 'json',
        statusCode: {
            409: function () {
                $("#existsModal").modal("toggle");
            },
            406: function () {
                $("#passwordModal").modal("toggle");
            },
            202: function () {
                $("#successModal").modal("toggle");
            }

        }
    })

}