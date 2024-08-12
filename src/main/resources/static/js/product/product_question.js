            //상품문의 등록
            $(document).ready(function() {
            $('#productqForm').on('submit', function(event) {
                event.preventDefault(); // 폼의 기본 제출 동작을 방지

                var formData = new FormData(this);
                var title = formData.get('title');
                var category = formData.get('productqCategorycode');
                var content = formData.get('content');
                var secret = formData.get('secret');
                var productNo = formData.get('productNo');
                var memberNo = $('#curmemberNo').val();

                // 날짜 포맷팅 함수
                function formatDate(dateString) {
                    var date = new Date(dateString);
                    return `${date.getFullYear().toString().slice(-2)}/${('0' + (date.getMonth() + 1)).slice(-2)}/${('0' + date.getDate()).slice(-2)} ${('0' + date.getHours()).slice(-2)}:${('0' + date.getMinutes()).slice(-2)}`;
                }

                $.ajax({
                    url: "/customer/user/productq_app", // 서버의 URL
                    type: "POST",
                    data: formData,
                    contentType: false,
                    processData: false,
                    success: function(response) {
                        if (response == 1) {
                            alert("상품 문의 등록"); // 성공 메시지 표시
                            $('#productqForm')[0].reset(); // 폼 필드 초기화
                            $('#productModal').modal('hide'); // 모달 닫기
                            loadComments(1);

                        } else if(response==0){
                            alert('로그인후 이용가능합니다.');
                            window.location.href = '/signin';
                        }
                        else {
                            alert('문의 제출에 실패했습니다: ' + response.message);
                        }
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        console.error('Error:', textStatus, errorThrown);
                        alert('문제 발생! 다시 시도해 주세요.');
                    }
                });

        const buttons = document.querySelectorAll('.btn-category');
        buttons.forEach(button => {
            button.addEventListener('click', function() {
                // 모든 버튼에서 active 클래스를 제거합니다.
                buttons.forEach(btn => btn.classList.remove('active'));
                // 클릭된 버튼에 active 클래스를 추가합니다.
            });
            document.getElementById('getAllButton').classList.add('active');
        });
    });
    })