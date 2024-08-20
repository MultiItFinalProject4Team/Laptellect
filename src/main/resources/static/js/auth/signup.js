$(document).ready(function() {
  let reg = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-_])(?=.*[0-9]).{8,15}$/;
  let isId = false;
  let isEmail = false;
  let isPassword = false;
  let isPassword2 = false;

  $("#insertBtn").prop("disabled", true);
  $(".emailVerifyButton").prop("disabled", true);
  $(".error-message").hide();

  function signupVisible() {
    if(isId && isEmail && isPassword && isPassword2) {
        $("#insertBtn").prop("disabled", false);
    } else {
        $("#insertBtn").prop("disabled", true);
    }
  }

  // 아이디 중복 체크
  $("#id").on("blur", function () {
      let userId = $("#id").val().trim();
      console.log(userId);
      if (userId != "") {
          $.ajax({
              url: "/api/check-id",
              type: "POST",
              data: { userName: userId },
              success: function (response) {
                  if (response === true) {
                      $('#id').addClass('is-invalid');
                      console.log("아이디 중복");
                      isId = false;
                      signupVisible();
                      $("#idError").show();
                      $("#idError2").hide();
                  } else {
                      console.log("아이디 중복 X");
                      $('#id').removeClass('is-invalid');
                      isId = true;
                      signupVisible();
                      $("#idError").hide();
                      $("#idError2").hide();
                  }
              },
              error: function () {
                  swal("실패", "아이디 중복 확인 실패", "error");
                  $("#idError").hide();
                  isId = false;
                  $("#insertBtn").prop("disabled", true);
              }
          });
      } else {
          $('#id').addClass('is-invalid');
          $("#idError2").show();
          isId = false;
          signupVisible();
      }
  });

  // 패스워드 정규식 검증
  $("#pass").on("blur", function () {
    let password = $(this).val();
    let password2 = $("#pass2").val();
    console.log(password);
    console.log(password2);

    if (!reg.test(password)) {
      $('#passwordError').show();
      $(this).addClass('is-invalid');
      $("#pass2").css({
        "pointer-events": "none",
        "user-select": "none"
      });

      isPassword = false;
      signupVisible();
    } else {
      $('#passwordError').hide();
      $(this).removeClass('is-invalid');
      $("#pass2").css({
        "pointer-events": "",
        "user-select": ""
      });
      isPassword = true;
      signupVisible();
    }

    if (password2 !==  "") {
        if (password !== password2) {
           $('#passwordError2').show();
           isPassword2 = false;
           signupVisible();
         } else {
           $('#passwordError2').hide();
           isPassword2 = true;
           signupVisible();
         }
      }
  });

  // 패스워드 일치 확인
  $("#pass2").on("blur", function () {
    let password = $("#pass").val();
    let password2 = $(this).val();

    console.log(password, password2);

    if (password !== password2) {
      $('#passwordError2').show();
      isPassword2 = false;
      signupVisible();
    } else {
      $('#passwordError2').hide();
      isPassword2 = true;
      signupVisible();
    }
  });

  // 이메일 중복 체크
  $("#emailInput").on("blur", function () {
    let email = $(this).val();
    console.log(email);

    $.ajax({
      url: "/api/check-email",
      type: "POST",
      data: { email: email },
      success: function (response) {
        if (response === true) {
          console.log("이메일 중복 완료");
          $('#emailInput').addClass('is-invalid');

          $("#emailError").show();
          $("#emailVerifyButton").prop("disabled", true);
        } else {
          console.log("이메일 중복 확인 완료");
          $('#emailInput').removeClass('is-invalid');
          $("#emailError").hide();
          $("#emailVerifyButton").prop("disabled", false);
        }
      },
      error: function () {
        console.log("이메일 확인 실패");
      },
    });
  });

  // 이메일 인증 메일 발송
  $("#emailVerifyButton").on("click", function () {
    let email = $('#emailInput').val();

    $.ajax({
      url: "/api/verify-email",
      type: "POST",
      data: { email: email },
      success: function (response) {
        if (response === true) {
          console.log("이메일 전송 완료");
          $('#emailInput').removeClass('is-invalid');
          swal("완료", "이메일 전송 완료", "success");
        } else {
          console.log("이메일 전송 실패");
          $('#emailInput').addClass('is-invalid');
          swal("실패", "이메일 전송 실패", "error");
        }
      },
      error: function () {
        console.log("이메일 연결 실패");
      },
    });
  });

  // 이메일 인증 번호 검증
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
          $('#emailVer').removeClass('is-invalid');
          isEmail = true;
          signupVisible();
        } else {
          console.log("틀린 인증번호");
          $("#emailError2").show();
          $('#emailVer').addClass('is-invalid');
          isEmail = false;
          signupVisible();
        }

      },
      error: function () {
        console.log("인증번호 확인 실패");
        isEmail = false;
        signupVisible();
      },
    });
  });

  // 회원 가입
  $("#insertBtn").on("click", function () {
    let memberName = $('#id').val();
    let password = $('#pass2').val();
    let email = $('#emailInput').val();

    console.log(memberName, password, email)

    $.ajax({
      url: "/api/signup",
      type: "POST",
      data: { memberName: memberName, password: password, email: email },
      success: function (response) {
        switch (response) {
          case 0:
            swal("성공", "회원 가입 완료", "success");
            window.location.href = "/";
            break;
          case 1:
            swal("오류", "중복된 아이디입니다.", "error");
            break;
          case 2:
            swal("오류", "중복된 이메일입니다.", "error");
            break;
          default:
            swal("오류", "회원 가입 중 오류가 발생했습니다.", "error");
        }
      },
      error: function () {
        console.log("회원가입 연결 실패");
      },
    });
  });
});