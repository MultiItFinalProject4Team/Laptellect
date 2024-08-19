function openKakaoLogin() {
        window.open("/signin/kakao", '_blank', 'width=600,height=425');
    }

    function openGoogleLogin() {
        window.open("/signin/google", '_blank', 'width=600,height=425');
    }

    function openFindId() {
        window.open("/find-id", '_blank', 'width=300,height=300');
    }

    function openFindPw() {
        window.open("/find-pw", '_blank', 'width=300,height=300');
    }

    $(document).ready(function(){
        window.addEventListener('message', function(event) {
            if (event.data === 'closeAndRedirect') {
                window.location.href = '/';
            }
        });

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