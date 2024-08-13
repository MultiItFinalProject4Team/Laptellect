        //상품문의 등록
        $(document).ready(function() {
            $(document).on('submit', '#productqForm',function(event) {
                event.preventDefault(); // 폼의 기본 제출 동작을 방지

                var formData = new FormData(this);
                console.log(formData.get('productNo'))

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

    //상품 문의 답변
    $(document).on('submit', '#productaForm',function(event) {
        event.preventDefault(); // 폼의 기본 제출 동작을 방지

        var formData = new FormData(this);
        console.log(formData.get('productqNo'));

        // 날짜 포맷팅 함수
        function formatDate(dateString) {
            var date = new Date(dateString);
            return `${date.getFullYear().toString().slice(-2)}/${('0' + (date.getMonth() + 1)).slice(-2)}/${('0' + date.getDate()).slice(-2)} ${('0' + date.getHours()).slice(-2)}:${('0' + date.getMinutes()).slice(-2)}`;
        }

        $.ajax({
            url: "/customer/admin/answer_productq", // 서버의 URL
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function(response) {
                if (response == 1) {
                    alert("상품 문의 답변 등록"); // 성공 메시지 표시
                    $('#productaForm')[0].reset(); // 폼 필드 초기화
                    $('#productAnswerModal').modal('hide'); // 모달 닫기
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

function QuestionApp(){
    $('#title').val('');
    $('#inputGroupSelect01').val('productq_opinion');
    $('#content').val('');
    $("input[name='secret'][value='N']").prop('checked', true);

    var button = $('#save_update');
    button.text('등록');
    var form = $('#updateForm');
    form.off();
    form.attr('id', 'productqForm');
    $('#productModal').modal('show');
}

function appReply(productqNo){
    $('#Atitle').val('');
    $('#AinputGroupSelect01').val('productq_opinion');
    $('#Acontent').val('');
    $('#AproductqNo').val(productqNo);
//    var button = $('#save_update');
//    button.text('등록');
//    var form = $('#updateaForm');
//    form.off();
//    form.attr('id', 'productaForm');
    $('#productAnswerModal').modal('show');
}