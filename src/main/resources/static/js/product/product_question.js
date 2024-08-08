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

                            $.ajax({
                                url: "/customer/user/get_AllproductqList", // 최신 데이터 가져오는 엔드포인트
                                type: "GET",
                                data: { productNo: productNo },
                                success: function(data) {
                                    // 서버에서 받아온 데이터로 #customer-box를 업데이트
                                    $('#customer-list').empty(); // 기존 내용 삭제
                                    data.forEach(function(productqList) {
                                    console.log(productqList)
                                        var newListItem = `
                                        <div class="question">
                                            <div class="question-header">
                                                <div class="question-author">
                                                    <span class="author-name">${productqList.memberName || '정보 없음'}</span>
                                                    <span class="question-date">${formatDate(productqList.createdAt)}</span>
                                                </div>
                                            </div>
                                            <div class="question-body">
                                                <div class="question-bodytop">
                                                    <p class="question-category">${productqList.productqCategoryCode === 'productq_opinion' ? '의견' : '문의'}</p>
                                                    <h4 class="question-title">${productqList.title || '제목 없음'}</h4>
                                                </div>
                                                <div class="question-content">
                                                    <p class="question-content">${productqList.content || '내용 없음'}</p>
                                                </div>
                                            </div>
                                            <hr class="question-hr">
                                        </div>
                                        `;
                                        $('#customer-list').append(newListItem);
                                    });
                                },
                                error: function(jqXHR, textStatus, errorThrown) {
                                    console.error('Error:', textStatus, errorThrown);
                                    alert('문제 발생! 다시 시도해 주세요.');
                                }
                            });

                        } else {
                            alert('문의 제출에 실패했습니다: ' + response.message);
                        }
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        console.error('Error:', textStatus, errorThrown);
                        alert('문제 발생! 다시 시도해 주세요.');
                    }
                });
            });
            })