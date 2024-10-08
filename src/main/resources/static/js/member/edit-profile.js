$(function () {
      $(document).ready(function () {
        $(".error-message").hide();

        $(".changeBtn").prop("disabled", true);
        $("#emailVerifyButton").prop("disabled", true);

        // 정규식 변수
        let reg = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-_])(?=.*[0-9]).{8,15}$/;
        
        $("#nickNameInput").on("blur", function () {
          let nickName = $(this).val();
          let regId = /[^a-zA-Z0-9가-힣ㄱ-ㅎ]/g;

          console.log(nickName);

          if(regId.test(nickName)) {
            $("#nickNameError").text("특수문자를 사용할 수 없습니다.");
            $("#nickNameError").show();
            $('#nickNameInput').addClass('is-invalid');
          } else {
            $.ajax({
              url: "/api/check-nickname",
              type: "POST",
              data: { nickName: nickName },
              success: function (response) {
                if (response === true) {
                  console.log("중복된 닉네임");
                  $("#nickNameError").text("중복된 닉네임 입니다.");
                  $("#nickNameError").show();
                  $('#nickNameInput').addClass('is-invalid');
                  $("#nickNameChangeBtn").prop("disabled", true);
                } else {
                  console.log("중복되지 않은 닉네임");
                  $("#nickNameError").hide();
                  $('#nickNameInput').removeClass('is-invalid');
                  $("#nickNameChangeBtn").prop("disabled", false);
                }
  
              },
              error: function () {
                console.log("닉네임 확인 실패");
                $('#nickNameInput').addClass('is-invalid');
              },
            });
          }

          
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
                swal("닉네임 변경 완료", "", "success");
                $('#nickNameModal').modal('hide');

                $("#nickName").prop("readonly", false);
                $("#nickName").val(nickName);
                $("#nickName").prop("readonly", true);
                $("#nickNameChangeBtn").prop("disabled", true);

              } else {
                console.log("닉네임 변경 실패");
                swal("닉네임 변경 실패", "", "error");
              }

            },
            error: function () {
              swal("닉네임 변경 실패", "", "error");
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
                console.log("이메일 중복 입니다.");
                $("#emailError").show();
                $('#emailInput').addClass('is-invalid');
                $("#emailVerifyButton").prop("disabled", true);

              } else {
                console.log("이메일 중복 아닙니다.");
                $("#emailError").hide();
                $('#emailInput').removeClass('is-invalid');
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
                swal('이메일 전송 완료!', '', 'success')
              } else {
                console.log("이메일 전송 실패");
                swal("이메일 전송 실패", "", "error");
              }
            }
          })
        }

        $("#emailVerifyButton").on("click", function () {
                sendEmailVerification();
        });

        $("#emailVer").on("blur", function () {
          let verifyCode = $(this).val();
          let email = $('#emailInput').val();
          console.log(verifyCode, email);

          $.ajax({
            url: "/api/check-verify-email",
            type: "POST",
            data: { verifyCode: verifyCode, email: email },
            success: function (response) {
              if (response === true) {
                console.log("인증번호 확인 완료");
                $("#emailError2").hide();
                $('#emailVer').removeClass('is-invalid');
                $("#emailChangeBtn").prop("disabled", false);
              } else {
                console.log("틀린 인증번호");
                $("#emailError2").show();
                $('#emailVer').addClass('is-invalid');
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
                swal("이메일 변경 완료", "", "success");
                $('#emailModal').modal('hide');

                $("#email").prop("readonly", false);
                $("#email").val(afterEmail);
                $("#email").prop("readonly", true);

                $("#emailChangeBtn").prop("disabled", true);
              } else {
                console.log("이메일 변경 실패");
                swal("이메일 또는 인증번호가 일치하지 않습니다.", "", "error");
              }

            },
            error: function () {
              swal("이메일 변경 확인 실패", "", "error");
            },
          });
        });

        // 비밀번호 검증 함수
        $("#beforePassword").on("blur", function () {
          let beforePassword = $(this).val();
          let afterPassword2 = $('#afterPassword2').val();
          console.log(beforePassword);

          $.ajax({
            url: "/api/check-password",
            type: "POST",
            data: { beforePassword: beforePassword },
            success: function (response) {
              if (response === true) {
                console.log("패스워드 확인 완료");
                $("#passwordError").hide();
                $('#beforePassword').removeClass('is-invalid');
                $('#afterPassword').prop('readonly', false);
                if(afterPassword2 !== "") {
                  $("#passwordChangeBtn").prop("disabled", false);
                }
              } else {
                console.log("틀린 비밀번호");
                $("#passwordError").show();
                $('#beforePassword').addClass('is-invalid');
                $('#afterPassword').prop('readonly', true);
                if(afterPassword2 !== "") {
                  $("#passwordChangeBtn").prop("disabled", true);
                }
              }

            },
            error: function () {
              console.log("비밀번호 확인 실패");
              $('#beforePassword').addClass('is-invalid');
              $('#afterPassword').prop('readonly', true);
            },
          });
        });

        $("#afterPassword").on("blur", function () {
          let beforePassword = $('#beforePassword').val();
          let afterPassword = $(this).val();
          let afterPassword2 = $('#afterPassword2').val();
          console.log(afterPassword);

          if(!reg.test(afterPassword)) {
            $('#passwordError2').show();
            $('#afterPassword').addClass('is-invalid');
            $('#afterPassword2').prop('readonly', true);

          } else {
            $('#passwordError2').hide();
            

            $.ajax({
              url: "/api/check-after-password",
              type: "POST",
              data: { beforePassword: beforePassword, afterPassword: afterPassword },
              success: function (response) {
                if (response === true) {
                  $('#passwordError3').hide();
                  $('#afterPassword').removeClass('is-invalid');
                  $('#afterPassword2').prop('readonly', false);
                } else {
                  $('#passwordError3').show();
                  $('#afterPassword').addClass('is-invalid');
                  $('#afterPassword2').prop('readonly', true);
                }
              },
              error: function () {
                console.log("비밀번호 검증 실패")
              },
            });
          }

          if (afterPassword2 !==  "") {
            if (afterPassword !== afterPassword2) {
              $('#passwordError4').show();
              $('#afterPassword2').addClass('is-invalid');
              $("#passwordChangeBtn").prop("disabled", true);
            } else {
              $('#passwordError4').hide();
              $('#afterPassword2').removeClass('is-invalid');
              $("#passwordChangeBtn").prop("disabled", false);
            }
          }
        });

        $("#afterPassword2").on("blur", function () {
          let afterPassword2 = $(this).val();
          let afterPassword = $('#afterPassword').val();
          console.log(afterPassword2);

          if (afterPassword2 !==  "") {
            if (afterPassword !== afterPassword2) {
              $('#passwordError4').show();
              $('#afterPassword2').addClass('is-invalid');
              $("#passwordChangeBtn").prop("disabled", true);
            } else {
              $('#passwordError4').hide();
              $('#afterPassword2').removeClass('is-invalid');
              $("#passwordChangeBtn").prop("disabled", false);
            }
          }
        });

        $("#btn-delete-id").on("click", function () {
          $.ajax({
            url: "/api/delete-member",
            type: "GET",
            success: function (response) {
              if (response === true) {
                swal("회원 탈퇴 성공", "", "success");
                setTimeout(function() {
                    window.location.href='/';
                }, 1500);
              } else {
                swal("회원 탈퇴 실패", "", "error");
              }

            },
            error: function () {
              swal("회원 탈퇴 에러", "", "error");
            },
          });
        });

        $("#passwordChangeBtn").on("click", function () {
          let afterPassword2 = $('#afterPassword2').val();
          let beforePassword = $('#beforePassword').val();
          console.log(beforePassword, afterPassword2);

          $.ajax({
            url: "/api/update-password",
            type: "POST",
            data: { beforePassword: beforePassword, afterPassword: afterPassword2 },
            success: function (response) {
              if (response === true) {
                swal("비밀번호 변경 완료", "", "success");
                $('#passwordModal').modal('hide');

                $("#password").prop("readonly", false);
                $("#password").val(afterPassword2);
                $("#password").prop("readonly", true);

                $("#passwordChangeBtn").prop("disabled", true);
              } else {
                console.log("비밀번호 변경 실패");
                swal("비밀번호 변경 실패", "", "error");
              }

            },
            error: function () {
              swal("비밀번호 변경 확인 실패", "", "error");
            },
          });
        });


        function sendSmsVerification() {
          let tel = $('#phoneNumInput').val().replace(/-/g, '');;

          $.ajax({
            url: "/api/verify-tel",
            type: "POST",
            data: { tel: tel },
            success: function (response) {
              if (response === true) {
                console.log("SMS 전송 완료");
                swal("SMS 전송 완료", "", "success");
              } else {
                console.log("SMS 전송 실패");
                swal("SMS 전송 실패", "", "error");
              }
            }
          })
        }

        $("#smsVerifyButton").on("click", function () {
                sendSmsVerification();
        });

        $("#phoneNumVer").on("blur", function () {

          let verifyCode = $(this).val()
          let tel = $('#phoneNumInput').val().replace(/-/g, '');;

          console.log(verifyCode);

          $.ajax({
            url: "/api/check-verify-tel",
            type: "POST",
            data: { verifyCode: verifyCode, tel: tel },
            success: function (response) {
              if (response === true) {
                console.log("인증번호 확인 완료");
                $("#phoneError").hide();
                $("#telChangeBtn").prop("disabled", false);
              } else {
                console.log("틀린 인증번호거나 전화번호가 다릅니다.");
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
          let changeTel = $('#phoneNumInput').val().replace(/-/g, '');
          let verifyCode = $('#phoneNumVer').val();
          console.log(changeTel, verifyCode);


          $.ajax({
            url: "/api/update-tel",
            type: "POST",
            data: { tel: changeTel, verifyCode: verifyCode },
            success: function (response) {
              if (response === true) {
                swal("휴대폰 번호 변경 완료", "", "success");
                $('#phoneModal').modal('hide');

                $("#phoneNumber").prop("readonly", false);
                $("#phoneNumber").val(changeTel);
                $("#phoneNumber").prop("readonly", true);

                $("#telChangeBtn").prop("disabled", true);
              } else {
                console.log("휴대폰 번호 또는 인증번호가 일치하지 않습니다.");
                swal("휴대폰 번호 또는 인증번호가 일치하지 않습니다.", "", "error");
              }

            },
            error: function () {
              swal("휴대폰 번호 변경 확인 실패", "", "error");
            },
          });
        });

      });
    });