function updateQuestion(productqNo){
    console.log(productqNo)
    $.ajax({
        url: '/customer/user/update_productq', // 데이터 요청을 보낼 URL
        type: 'GET',
        data: { productqNo: productqNo }, // 요청에 포함될 데이터
        success: function(response) {
            $('#title').val(response.title);
            $('#inputGroupSelect01').val(response.productqCategoryCode);
            $('#content').val(response.content);
            $("input[name='secret'][value='" + response.secret + "']").prop('checked', true);
            $('#productNo').val(response.productNo);
            $('#productqNo').val(response.productqNo);

            // 모달 표시
            console.log(response)
            var button = $('#save_update');
            button.text('수정');
            var form = $('#productqForm');
            form.off();
            form.attr('id', 'updateForm');
            console.log(form);
            $('#productModal').modal('show');
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error("AJAX 요청 오류:", textStatus, errorThrown);
            alert("데이터를 로드하는 중 오류가 발생했습니다.");
        }
    });
}

$(document).ready(function() {
    $(document).on('submit', '#updateForm',function(event) {
        event.preventDefault(); // 폼의 기본 제출 동작을 방지

        var formData = new FormData(this);

        $.ajax({
            url: "/customer/user/update_productq", // 서버의 URL
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function(response) {
                if (response == 1) {
                    alert("상품 문의 수정 성공"); // 성공 메시지 표시
                    $('#updateForm')[0].reset(); // 폼 필드 초기화
                    $('#productModal').modal('hide'); // 모달 닫기
                    loadComments(1); // 댓글 로드 함수 호출
                } else {
                    alert('문의 제출에 실패했습니다: ' + response.message); // 실패 메시지 표시
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.error('Error:', textStatus, errorThrown);
                alert('문제 발생! 다시 시도해 주세요.'); // 오류 메시지 표시
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
});
function confirmDelete(productqNo) {
    // 확인 대화상자를 띄우고 사용자의 응답을 체크
    var isConfirmed = confirm("정말로 삭제하시겠습니까?");

    if (isConfirmed) {
        // 사용자가 확인 버튼을 클릭한 경우, 삭제 요청을 수행
        deleteQuestion(productqNo);
    } else {
        // 사용자가 취소 버튼을 클릭한 경우, 아무 작업도 하지 않음
        console.log("삭제 작업이 취소되었습니다.");
    }
}

function deleteQuestion(productqNo) {
    console.log("삭제할 질문 번호: ", productqNo);

    $.ajax({
        url: "/customer/user/delete_productq",
        type: "POST",
        data: {
            productqNo: productqNo,
        },
        success: function(response) {
            console.log("서버 응답: ", response);
            if (response === 1) {
                loadComments(1);
            } else {
                console.error("서버에서 예상하지 못한 응답을 받았습니다.");
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error("AJAX 요청 오류:", textStatus, errorThrown);
            alert("댓글을 삭제하는 중 오류가 발생했습니다.");
        }
    });
}