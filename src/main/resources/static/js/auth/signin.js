function openKakaoLogin() {
        window.open("/signin/kakao", '_blank', 'width=600,height=425');
    }

    function openGoogleLogin() {
        window.open("/signin/google", '_blank', 'width=600,height=425');
    }

    function openFindId() {
        window.open("/find-id", '_blank', 'width=300,height=350');
    }

    function openFindPw() {
        window.open("/find-pw", '_blank', 'width=300,height=350');
    }

    $(document).ready(function(){
        window.addEventListener('message', function(event) {
            switch(event.data) {
                case 'SignUp':
                    swal("회원가입 성공", "", "success");

                    setTimeout(function() {
                        swal.close();
                        window.location.href = '/signin';
                    }, 1500);
                    break;
                case 'SignIn':
                    swal("로그인 성공", "", "success");

                    setTimeout(function() {
                        swal.close();
                        window.location.href = '/';
                    }, 1500);
                    break;
                case 'Fail':
                    swal("에러", "다시 시도해 주세요", "error");

                    setTimeout(function() {
                        swal.close();
                        window.location.href = '/';
                    }, 1500);
                    break;
            }
        }, false);

        $("#kakao-login").on("click", function() {
            openKakaoLogin();
        });

        $("#google-login").on("click", function() {
            openGoogleLogin();
        });

        $("#find-id").on("click", function() {
            openFindId();
        });

        $("#find-pw").on("click", function() {
            openFindPw();
        });
    });