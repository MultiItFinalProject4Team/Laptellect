$(document).ready(function() {
  let reg = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-_])(?=.*[0-9]).{8,15}$/;
  $(".error-message").hide();

  function areAnyErrorMessagesVisible() {
    if($('.error-message:hidden').length !== $('.error-message').length) {
      $("#insertBtn").prop("disabled", true);
    }
  }

  $("#insertBtn").prop("disabled", true);
  $(".emailVerifyButton").prop("disabled", true);

  // 아이디 중복 체크
  $("#id").on("blur", function () {
    areAnyErrorMessagesVisible();
    let userId = $("#id").val();
    console.log(userId);

    $.ajax({
        url: "/api/check-id",
        type: "POST",
        data: { userName: userId },
        success: function (response) {
          if (response === true) {
            $('#id').addClass('is-invalid');
            console.log("아이디 중복")
            $("#idError").show();
          } else {
            console.log("아이디 중복 X");
            $('#id').removeClass('is-invalid');
            $("#idError").hide();
          }
        },
        error: function () {
          alert("아이디 중복 확인 실패")
          $("#idError").hide();
          $("#insertBtn").prop("disabled", true);
        },
    });
  });

  // 패스워드 정규식 검증
  $("#pass").on("blur", function () {
    areAnyErrorMessagesVisible();
    let password = $(this).val();
    console.log(password);

    if (!reg.test(password)) {
      $('#passwordError').show();
      $(this).addClass('is-invalid');
      $("#pass2").css({
        "pointer-events": "none",
        "user-select": "none"
      });
      $("#insertBtn").prop("disabled", true);
    } else {
      $('#passwordError').hide();
      $(this).removeClass('is-invalid');
      $("#pass2").css({
        "pointer-events": "",
        "user-select": ""
      });
    }
  });

  // 패스워드 일치 확인
  $("#pass2").on("blur", function () {
    areAnyErrorMessagesVisible();
    let password = $("#pass").val();
    let password2 = $(this).val();

    console.log(password, password2);

    if (password !== password2) {
      $('#passwordError2').show();
      $("#insertBtn").prop("disabled", true);
    } else {
      $('#passwordError2').hide();
    }
  });

  // 이메일 중복 체크
  $("#emailInput").on("blur", function () {
    areAnyErrorMessagesVisible();
    let email = $(this).val();
    console.log(email);

    $.ajax({
      url: "/api/check-email",
      type: "POST",
      data: { email: email },
      success: function (response) {
        if (response === true) {
          console.log("이메일 중복 확인 완료");
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
          alert("이메일 전송 완료");
        } else {
          console.log("이메일 전송 실패");
          $('#emailInput').addClass('is-invalid');
          alert("이메일 전송 실패");
        }
      },
      error: function () {
        console.log("이메일 연결 실패");
      },
    });
  });

  // 이메일 인증 번호 검증
  $("#emailVer").on("blur", function () {
    areAnyErrorMessagesVisible();
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
          $('#emailInput').removeClass('is-invalid');
          $("#insertBtn").prop("disabled", false);
        } else {
          console.log("틀린 인증번호");
          $("#emailError2").show();
          $('#emailInput').addClass('is-invalid');
          $("#insertBtn").prop("disabled", true);
        }

      },
      error: function () {
        console.log("인증번호 확인 실패");
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
            alert("회원 가입 완료.");
            window.location.href = "/";
            break;
          case 1:
            alert("중복된 아이디입니다.");
            break;
          case 2:
            alert("중복된 이메일입니다.");
            break;
          default:
            alert("회원 가입 중 오류가 발생했습니다.");
        }
      },
      error: function () {
        console.log("회원가입 연결 실패");
      },
    });
  });
});