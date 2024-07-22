$(function() {
    $(document).ready(function() {
            $('#emailError').hide();
            $('#nameError').hide();
            $('#sing-up').prop('disabled', true);

            // 정규식 변수
            let reg = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$/

            function isEmailAndNameAndPassWord() {
                $('#sing-up').prop('disabled', $('#emailError').is(':visible') || $('#nameError').is(':visible') || $('#passwordError').is(':visible') || $('#passwordError2').is(':visible'));
            }

            $('#email').on('blur', function() {
                let email = $(this).val();
                console.log(email);

                if (email.length == 0) {
                    $('#emailError').hide();
                    isEmailAndNameAndPassWord();
                    return;
                }

                $.ajax({
                    url: '/member/isEmail',
                    type: 'POST',
                    data: { email: email },
                    success: function(response) {
                        if (response === true) {
                            console.log("중복된 이메일 입니다");
                            $('#emailError').show();
                        } else if (response === false) {
                            console.log("중복되지 않은 이메일 입니다");
                            $('#emailError').hide();
                        }
                        isEmailAndNameAndPassWord();
                    },
                    error: function() {
                        console.log('이메일 중복 확인 실패');
                        isEmailAndNameAndPassWord();
                    }
                });
            });

            $('#userName').on('blur', function() {
                let name = $(this).val();
                console.log(name);

                if (name.length == 0) {
                    $('#nameError').hide();
                    isEmailAndNameAndPassWord();
                    return;
                }

                $.ajax({
                    url: '/member/isName',
                    type: 'POST',
                    data: { name: name },
                    success: function(response) {
                        if (response === true) {
                            console.log("중복된 닉네임 입니다");
                            $('#nameError').show();
                        } else if (response === false) {
                            console.log("중복되지 않은 닉네임 입니다");
                            $('#nameError').hide();
                        }
                        isEmailAndNameAndPassWord();
                    },
                    error: function() {
                        console.log('닉네임 중복 확인 실패');
                        isEmailAndNameAndPassWord();
                    }
                });
            });

                // 비밀번호 확인
                $('#password, #confirmPassword').on('blur', function() {
                    let password = $('#password').val();
                    let confirmPassword = $('#confirmPassword').val();

                    if (password !== confirmPassword) {
                        $('#passwordError').show();
                    } else {
                        $('#passwordError').hide();
                    }

                    if (!reg.test(password)) {
                        $('#passwordError2').show();
                    } else {
                        $('#passwordError2').hide();
                    }


                    isEmailAndNameAndPassWord();
                });
            });

        });





// KaKao Map 주소 API
function findAddr() {
    new daum.Postcode({
        oncomplete: function(data) {
            var roadAddr = data.roadAddress; // 도로명 주소 변수
            var jibunAddr = data.jibunAddress; // 지번 주소 변수

            // jQuery를 사용하여 요소 값 설정
            if (roadAddr !== '') {
                $('#address').val(roadAddr);
            } else if (jibunAddr !== '') {
                $('#address').val(jibunAddr);
            }
        }
    }).open();
}