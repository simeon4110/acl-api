// Display the users table.
var table = $('#users').DataTable({
    'ajax': {
        'url': '/admin/user/all',
        'dataSrc': ''
    },
    "columns": [
        {"mData": "username"},
        {"mData": "admin"},
        {
            // Render the date in a human readable format.
            "mData": "addedAt",
            "render": function (data, type, row) {
                if (type === "sort" || type === "type") {
                    return data;
                }
                return moment(data).format("MM-DD-YYYY");
            }
        },
        {
            // Create the password reset button.
            "mData": "username",
            "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html("<button type='button' class='btn btn-primary btn-sm' data-toggle='modal'" +
                    "data-target='#resetModal' data-user='" + sData + "'>Reset Password</button>")
            }
        },
        {
            // Create the user modify button.
            "mData": "username",
            "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html("<button type='button' class='btn btn-primary btn-sm' data-toggle='modal'" +
                    "data-target='#modifyModal' data-user='" + sData + "'>Modify User</button>")
            }
        },
        {
            // Create the delete user button.
            "mData": "username",
            "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
                $(nTd).html("<button type='button' class='btn btn-danger btn-sm' data-toggle='modal'" +
                    "data-target='#deleteModal' data-user='" + sData + "'>Delete User</button>");
            }
        }
    ]
});

// Format the modal for password resets.
$("#resetModal").on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    var username = button.data('user');
    var modal = $(this);

    modal.find('.modal-title').text('Reset password for: ' + username);
    $("#resetUsername").val(username);
    $("#password").val("");
    $("#password1").val("");
});

// Format the modal for user modification.
$("#modifyModal").on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    var username = button.data('user');
    var modal = $(this);

    // Get the user's current data.
    $.ajax({
        url: '/admin/user/get/' + username,
        dataType: "json",
        success: function (data) {
            var response = data;
            modal.find('.modal-title').text('Modifying user: ' + username);
            console.log(response);
            $("#modifyUsername").val(username);
            $("#email").val(response['email']);
            if (response['admin'] === true) {
                $("#isAdmin select").val("true")
            } else {
                $("#isAdmin select").val("false")
            }
        },
        fail: function () {
            $("#failModal").modal("toggle");
        }
    });

});

// Format the modal for user deletion.
$("#deleteModal").on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    var username = button.data('user');
    var modal = $(this);

    modal.find('.modal-title').text("Delete " + username + "?");
    $("#deleteUsername").val(username);
});

// Do the password reset via PUT ajax request.
function resetPassword() {
    var username = $("#resetUsername").val();
    var password = $("#password").val();
    var password1 = $("#password1").val();

    var data = {"username": username, "password": password, "password1": password1};

    $("#resetModal").modal("toggle");

    $.ajax({
        url: '/admin/user/modify/password',
        type: 'put',
        data: JSON.stringify(data),
        headers: {
            "Content-Type": "application/json"
        },
        dataType: 'json',
        statusCode: {
            202: function () {
                $("#successModal").modal("toggle");
                table.ajax.reload();
            },
            406: function () {
                $("#failModal").modal("toggle");
            }
        }
    })


}

// Do the user modification via a PUT ajax request.
function modifyUser() {
    var username = $("#modifyUsername").val();
    var email = $("#email").val();
    var isAdmin = $("#isAdmin").val();

    var data = {"username": username, "email": email, "admin": isAdmin};

    $("#modifyModal").modal("toggle");

    $.ajax({
        url: '/admin/user/modify',
        type: 'put',
        data: JSON.stringify(data),
        headers: {
            "Content-Type": "application/json"
        },
        dataType: 'json',
        statusCode: {
            202: function () {
                $("#successModal").modal("toggle");
                table.ajax.reload();
            },
            406: function () {
                $("#failModal").modal("toggle");
            }
        }
    })

}

// Do the user deletion via a DELETE ajax request.
function deleteUser() {
    var username = $("#deleteUsername").val();
    var data = {"username": username};

    $("#deleteModal").modal("toggle");

    $.ajax({
        url: '/admin/user/delete',
        type: 'delete',
        data: JSON.stringify(data),
        headers: {
            "Content-Type": "application/json"
        },
        dataType: 'json',
        statusCode: {
            202: function () {
                $("#successModal").modal("toggle");
                table.ajax.reload();
            },
            406: function () {
                $("#failModal").modal("toggle");
            }
        }
    })

}
