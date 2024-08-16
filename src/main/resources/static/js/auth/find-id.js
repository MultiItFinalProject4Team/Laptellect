$(function () {
    $(document).ready(function () {
        $(".error-message").hide();
        $("#send-email").prop("disabled", true);

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
                        $("#send-email").prop("disabled", false);
                    } else {
                        console.log("존재하지 않는 이메일 입니다.");
                        $("#emailError").show();
                        $("#send-email").prop("disabled", true);

                    }
                },
                error: function () {
                    console.log("이메일 확인 실패");
                },
            });
        });

        function sendEmail() {
            let email = $('#email').val();

            $.ajax({
                url: "/api/find-user-id",
                type: "POST",
                data: { email: email },
                success: function (response) {
                    if (response === true) {
                        console.log("이메일 전송 완료");
                        alert("이메일 전송 완료");
                        window.close();
                    } else {
                        console.log("이메일 전송 실패");
                        alert("이메일 전송 실패");
                    }
                }
            });
        }

        $("#send-email").on("click", function () {
            sendEmail();
        });
    });
});