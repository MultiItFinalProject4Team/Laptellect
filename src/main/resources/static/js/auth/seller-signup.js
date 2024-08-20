$(document).ready(function() {
  let reg = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-_])(?=.*[0-9]).{8,15}$/;
  let isId = false;
  let isEmail = false;
  let isPassword = false;
  let isPassword2 = false;
  let isRegistrationNo = false;

  signupVisible();
  $(".emailVerifyButton").prop("disabled", true);
  $(".error-message").hide();

  function nullCheck(string) {
    if(string !== null && string.trim() !== "") {
      return true;
    } else {
      return false;
    }
  }

  function signupVisible() {
    if(isId && isEmail && isPassword && isPassword2 && isRegistrationNo) {
        $("#insertBtn").prop("disabled", false);
    } else {
        $("#insertBtn").prop("disabled", true);
    }
  }

  // 아이디 중복 체크
  $("#id").on("blur", function () {
      let userId = $("#id").val().trim();
      console.log(userId);
      if (nullCheck(userId)) {
          $.ajax({
              url: "/api/check-id",
              type: "POST",
              data: { userName: userId },
              success: function (response) {
                  if (response === true) {
                      $('#id').addClass('is-invalid');
                      console.log("아이디 중복");
                      isId = false;
                      $("#idError").show();
                      $("#idError2").hide();
                  } else {
                      console.log("아이디 중복 X");
                      $('#id').removeClass('is-invalid');
                      isId = true;
                      $("#idError").hide();
                      $("#idError2").hide();
                  }
                  signupVisible();
              },
              error: function () {
                  alert("아이디 중복 확인 실패");
                  $("#idError").hide();
                  isId = false;
                  $("#insertBtn").prop("disabled", true);
                  signupVisible();
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
         } else {
           $('#passwordError2').hide();
           isPassword2 = true;
         }
         signupVisible();
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
    } else {
      $('#passwordError2').hide();
      isPassword2 = true;
    }
    signupVisible();
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
        signupVisible();
      },
      error: function () {
        console.log("이메일 확인 실패");
        signupVisible();
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
          swal("이메일 전송 완료", "", "success");
          alert("이메일 전송 완료");
        } else {
          console.log("이메일 전송 실패");
          $('#emailInput').addClass('is-invalid');
          swal("이메일 전송 실패", "", "error");
          alert("이메일 전송 실패");
        }
      },
      error: function () {
        swal("이메일 연결 실패");
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

  // 판매자 회원가입
  function checkRegistrationNo() {
    let ownerName = $('#ownerName').val();
    let company = $('#company').val();
    let businessDate = $('#businessDate').val();
    let registrationNo = $('#registrationNo').val();
    $("#registrationNoError2").hide();

    if(nullCheck(ownerName) && nullCheck(company) && nullCheck(businessDate) && nullCheck(registrationNo)) {
      $.ajax({
        url: "/api/check-registration-no",
        type: "POST",
        data: { ownerName: ownerName, businessDate: businessDate, registrationNo: registrationNo },
        success: function (response) {
          switch(response) {
            case 1:
              console.log("사업자 등록번호 검증 성공");
              isRegistrationNo = true;
              $("#registrationNoError").hide();
              break;
            case 2:
              console.log("존재하지 않는 사업자 등록 번호");
              isRegistrationNo = false;
              $("#registrationNoError").show();
              break;
            case 3:
              console.log("파라미터 Null");
              isRegistrationNo = false;
              $("#registrationNoError").show();
              break;
          }
          signupVisible();
        },
        error: function () {
          console.log("사업자 등록번호 검증 에러");
          isRegistrationNo = false;
          $("#registrationNoError2").show();
          signupVisible();
        },
      });
    } else {
      isRegistrationNo = false;
      $("#registrationNoError2").show();
      signupVisible();
    }
  }
  

  // 대표자명 검증
  $("#ownerName").on("blur", function () {
    checkRegistrationNo();
  });

  // 회사명 검증
  $("#company").on("blur", function () {
    checkRegistrationNo();
  });

  // 개업일자 검증
  $("#businessDate").on("blur", function () {
    checkRegistrationNo();
  });

  // 사업자 등록번호 검증
  $("#registrationNo").on("blur", function () {
    checkRegistrationNo();
  });


  // 회원 가입
  $("#insertBtn").on("click", function () {
    let memberName = $('#id').val();
    let password = $('#pass2').val();
    let email = $('#emailInput').val();
    let loginType = "seller"
    let ownerName = $('#ownerName').val();
    let companyName = $('#company').val();
    let businessDate = $('#businessDate').val();
    let registrationNo = $('#registrationNo').val();

    $.ajax({
      url: "/api/signup",
      type: "POST",
      data: { memberName: memberName, password: password, email: email, loginType: loginType, companyName: companyName, ownerName: ownerName, businessDate: businessDate, registrationNo: registrationNo},
      success: function (response) {
        switch (response) {
          case 0:
            swal("판매자 회원 가입 완료.", "", "success");
            window.location.href = "/";
            break;
          case 1:
            swal("중복된 아이디입니다.", "", "warning");
            break;
          case 2:
            swal("중복된 이메일입니다.", "", "warning");
            break;
          default:
            swal("회원 가입 중 오류가 발생했습니다.");
        }
      },
      error: function () {
        console.log("회원가입 연결 실패");
      },
    });
  });
});