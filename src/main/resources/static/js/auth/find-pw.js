$(function () {
    $(document).ready(function () {
        $(".error-message").hide();
        $("#send-email").prop("disabled", true);

        let isUserId = false;
        let isEmail = false;

        function checkInput() {
            if (isUserId === true && isEmail === true) {
                $("#send-email").prop("disabled", false);
            } else {
                $("#send-email").prop("disabled", true);
            }
        }

        $("#userid").on("blur", function () {
            let userId = $(this).val();
            console.log(userId);

            $.ajax({
                url: "/api/check-id",
                type: "POST",
                data: { userName: userId },
                success: function (response) {
                    if (response === true) {
                        console.log("존재하는 아이디 입니다.");
                        $("#idError").hide();
                        isUserId = true;
                        checkInput();
                    } else {
                        console.log("존재하지 않는 이메일 입니다.");
                        $("#idError").show();
                        isUserId = false;
                        checkInput();
                    }
                },
                error: function () {
                    console.log("아이디 확인 실패");
                },
            });
        });

        $("#email").on("blur", function () {
            let email = $(this).val();
            console.log(email);

            $.ajax({
                url: "/api/check-email",
                type: "POST",
                data: { email: email },
                success: function (response) {
                    if (response === true) {
                        console.log("존재하는 이메일 입니다.");
                        $("#emailError").hide();
                        isEmail = true;
                        checkInput();
                    } else {
                        console.log("존재하지 않는 이메일 입니다.");
                        $("#emailError").show();
                        isEmail = false;
                        checkInput();
                    }
                },
                error: function () {
                    console.log("이메일 확인 실패");
                },
            });
        });

        function sendEmail() {
            let userId = $('#userid').val();
            let email = $('#email').val();

            $.ajax({
                url: "/api/send-temp-password",
                type: "POST",
                data: { userId: userId, email: email },
                success: function (response) {
                    switch (response) {
                        case 1:
                            swal('임시 비밀번호 전송 완료', '', 'success');
                            window.close();
                            break;
                        case 2:
                            swal('이메일 전송 실패', '', 'error');
                            break;
                    }
                }
            });
        }

        $("#send-email").on("click", function () {
            sendEmail();
        });
    });
});