$(function () {
      $(document).ready(function () {
        $(".error-message").hide();

        $(".changeBtn").prop("disabled", true);
        $("#emailVerifyButton").prop("disabled", true);

        // 정규식 변수
        let reg = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$/;

        $("#nickNameInput").on("blur", function () {
          let nickName = $(this).val();
          console.log(nickName);

          $.ajax({
            url: "/api/check-nickname",
            type: "POST",
            data: { nickName: nickName },
            success: function (response) {
              if (response === true) {
                console.log("중복된 닉네임");
                $("#nickNameError").show();
                $("#nickNameChangeBtn").prop("disabled", true);
              } else {
                console.log("중복되지 않은 닉네임");
                $("#nickNameError").hide();
                $("#nickNameChangeBtn").prop("disabled", false);
              }

            },
            error: function () {
              console.log("닉네임 확인 실패");
            },
          });
        });

        $("#nickNameChangeBtn").on("click", function () {
          let nickName = $('#nickNameInput').val();
          console.log(nickName);

          $.ajax({
            url: "/api/update-nickname",
            type: "POST",
            data: { nickName: nickName},
            success: function (response) {
              if (response === true) {
                alert("닉네임 변경 완료");
                $('#nickNameModal').modal('hide');

                $("#nickName").prop("readonly", false);
                $("#nickName").val(nickName);
                $("#nickName").prop("readonly", true);
                $("#nickNameChangeBtn").prop("disabled", true);

              } else {
                console.log("닉네임 변경 실패");
                alert("닉네임 변경 실패")
              }

            },
            error: function () {
              alert("닉네임 변경 실패")
            },
          });
        });

        $("#emailInput").on("blur", function () {
          let afterEmail = $(this).val();
          console.log(afterEmail);

          $.ajax({
            url: "/api/check-email",
            type: "POST",
            data: { email: afterEmail },
            success: function (response) {
              if (response === true) {
                console.log("이메일 중복 확인 완료");
                $("#emailError").show();
                $("#emailVerifyButton").prop("disabled", true);

              } else {
                console.log("이메일 중복 확인 실패");
                $("#emailError").hide();
                $("#emailVerifyButton").prop("disabled", false);
              }

            },
            error: function () {
              console.log("이메일 확인 실패");
            },
          });
        });

        function sendEmailVerification() {
          let afterEmail = $('#emailInput').val();

          $.ajax({
            url: "/api/verify-email",
            type: "POST",
            data: { email: afterEmail },
            success: function (response) {
              if (response === true) {
                console.log("이메일 전송 완료");
                alert("이메일 전송 완료");
              } else {
                console.log("이메일 전송 실패");
                alert("이메일 전송 실패");
              }
            }
          })
        }

        $("#emailVerifyButton").on("click", function () {
                sendEmailVerification();
        });

        $("#emailVer").on("blur", function () {
          let verifyCode = $(this).val();
          console.log(verifyCode);

          $.ajax({
            url: "/api/check-verify-email",
            type: "POST",
            data: { verifyCode: verifyCode },
            success: function (response) {
              if (response === true) {
                console.log("인증번호 확인 완료");
                $("#emailError2").hide();
                $("#emailChangeBtn").prop("disabled", false);
              } else {
                console.log("틀린 인증번호");
                $("#emailError2").show();
                $("#emailChangeBtn").prop("disabled", true);
              }

            },
            error: function () {
              console.log("인증번호 확인 실패");
            },
          });
        });

        $("#emailChangeBtn").on("click", function () {
          let afterEmail = $('#emailInput').val();
          let verifyCode = $('#emailVer').val();
          console.log(afterEmail, verifyCode);


          $.ajax({
            url: "/api/update-email",
            type: "POST",
            data: { email: afterEmail, verifyCode: verifyCode },
            success: function (response) {
              if (response === true) {
                alert("이메일 변경 완료");
                $('#emailModal').modal('hide');

                $("#email").prop("readonly", false);
                $("#email").val(afterEmail);
                $("#email").prop("readonly", true);

                $("#emailChangeBtn").prop("disabled", true);
              } else {
                console.log("이메일 변경 실패");
                alert("이메일 변경 실패")
              }

            },
            error: function () {
              alert("이메일 변경 확인 실패")
            },
          });
        });




        // $("#beforePassword").on("blur", function () {
        //   let beforePassword = $(this).val();
        //   console.log(beforePassword);

        //   $.ajax({
        //     url: "/api/check-password",
        //     type: "POST",
        //     data: { beforePassword: beforePassword },
        //     success: function (response) {
        //       if (response === true) {
        //         console.log("패스워드 확인 완료");
        //         $("#passwordError").hide();
        //       } else {
        //         console.log("틀린 비밀번호");
        //         $("#passwordError").show();
        //         $("#emailChangeBtn").prop("disabled", true);
        //       }

        //     },
        //     error: function () {
        //       console.log("비밀번호 확인 실패");
        //     },
        //   });
        // });


        // $("#beforePassword").on("blur", function () {
        //   let beforePassword = $(this).val();
        //   console.log(beforePassword);

        //   $.ajax({
        //     url: "/api/check-password",
        //     type: "POST",
        //     data: { beforePassword: beforePassword },
        //     success: function (response) {
        //       if (response === true) {
        //         console.log("패스워드 확인 완료");
        //         $("#passwordError").hide();
        //         $("#afterPassword").prop("readonly", false);
        //       } else {
        //         console.log("틀린 비밀번호");
        //         $("#passwordError").show();
        //         $("#afterPassword").prop("readonly", true);
        //         $("#passwordChangeBtn").prop("disabled", true);
        //       }

        //     },
        //     error: function () {
        //       console.log("비밀번호 확인 실패");
        //     },
        //   });
        // });

        // $("#afterPassword").on("blur", function () {
        //   let afterPassword = $(this).val();
        //   console.log(afterPassword);


        // });

        // $("#emailChangeBtn").on("click", function () {
        //   let afterEmail = $('#emailInput').val();
        //   let verifyCode = $('#emailVer').val();
        //   console.log(afterEmail, verifyCode);


        //   $.ajax({
        //     url: "/api/update-password",
        //     type: "POST",
        //     data: { email: afterEmail, verifyCode: verifyCode },
        //     success: function (response) {
        //       if (response === true) {
        //         alert("이메일 변경 완료");
        //         $('#emailModal').modal('hide');

        //         $("#email").prop("readonly", false);
        //         $("#email").val(afterEmail);
        //         $("#email").prop("readonly", true);

        //         $("#emailChangeBtn").prop("disabled", true);
        //       } else {
        //         console.log("이메일 변경 실패");
        //         alert("이메일 변경 실패")
        //       }

        //     },
        //     error: function () {
        //       alert("이메일 변경 확인 실패")
        //     },
        //   });
        // });













        function sendSmsVerification() {
          let tel = $('#phoneNumInput').val();

          $.ajax({
            url: "/api/verify-tel",
            type: "POST",
            data: { tel: tel },
            success: function (response) {
              if (response === true) {
                console.log("SMS 전송 완료");
                alert("SMS 전송 완료");
              } else {
                console.log("SMS 전송 실패");
                alert("SMS 전송 실패");
              }
            }
          })
        }

        $("#smsVerifyButton").on("click", function () {
                sendSmsVerification();
        });

        $("#phoneNumVer").on("blur", function () {
          let verifyCode = $(this).val();
          console.log(verifyCode);

          $.ajax({
            url: "/api/check-verify-tel",
            type: "POST",
            data: { verifyCode: verifyCode },
            success: function (response) {
              if (response === true) {
                console.log("인증번호 확인 완료");
                $("#phoneError").hide();
                $("#telChangeBtn").prop("disabled", false);
              } else {
                console.log("틀린 인증번호");
                $("#phoneError").show();
                $("#telChangeBtn").prop("disabled", true);
              }

            },
            error: function () {
              console.log("인증번호 확인 실패");
              $("#telChangeBtn").prop("disabled", true);
            },
          });
        });

        $("#telChangeBtn").on("click", function () {
          let tel = $('#phoneNumInput').val();
          let verifyCode = $('#phoneNumVer').val();
          console.log(tel, verifyCode);


          $.ajax({
            url: "/api/update-tel",
            type: "POST",
            data: { tel: tel, verifyCode: verifyCode },
            success: function (response) {
              if (response === true) {
                alert("휴대폰 번호 변경 완료");
                $('#phoneModal').modal('hide');

                $("#phoneNumber").prop("readonly", false);
                $("#phoneNumber").val(tel);
                $("#phoneNumber").prop("readonly", true);

                $("#telChangeBtn").prop("disabled", true);
              } else {
                console.log("휴대폰 번호 변경 실패");
                alert("휴대폰 번호 변경 실패")
              }

            },
            error: function () {
              alert("휴대폰 번호 변경 확인 실패")
            },
          });
        });

      });
    });