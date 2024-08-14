// DOM이 로드된 후 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', function() {
    const reviewModal = new bootstrap.Modal(document.getElementById('reviewModal'));
    const reviewContent = document.getElementById('reviewContent');
    const submitReviewButton = document.getElementById('submitReviewButton');

    // 리뷰 내용 입력 이벤트 리스너
    reviewContent.addEventListener('input', function() {
        const isEmpty = this.value.trim() === '';
        submitReviewButton.disabled = isEmpty;
    });

    // 리뷰 모달 열기 버튼 이벤트 리스너
    const reviewModalButton = document.getElementById('reviewModalButton');
    if (reviewModalButton) {
        reviewModalButton.addEventListener('click', function() {
            reviewModal.show();
        });
    }

    // 리뷰 제출 버튼 이벤트 리스너 (onclick 속성 제거)
    submitReviewButton.addEventListener('click', submitReview);
});

// 리뷰 제출
function submitReview() {
    const isPurchased = document.getElementById('isPurchased').value === 'true';

    if (!isPurchased) {
        alert("구매하신 상품이 아니거나, 이미 리뷰 등록을 완료하신 상품입니다.");
        bootstrap.Modal.getInstance(document.getElementById('reviewModal')).hide();
        return;
    }

    const memberNo = document.getElementById('memberNo').value;
    const productNo = document.getElementById('productNo2').value;
    const content = document.getElementById('reviewContent').value;
    const rating = document.getElementById('reviewRating').value;
    const imPortId = document.getElementById('imPortId').value;

    // 리뷰 데이터 준비
    const reviewData = {
        memberNo: memberNo,
        productNo: productNo,
        content: content,
        rating: rating,
        imPortId: imPortId
    };

    // 확인 메시지 표시
    if (confirm("리뷰를 작성하게 되면 주문 취소가 불가능해집니다. \n리뷰를 등록하시겠습니까?")) {
        // 사용자가 확인을 누른 경우에만 서버로 요청 전송
        fetch('/payment/reviews', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(reviewData),
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // 성공 시 모달 닫기 및 페이지 새로고침
                bootstrap.Modal.getInstance(document.getElementById('reviewModal')).hide();
                window.location.reload();
            } else {
                // 실패 시 오류 메시지 표시
                alert(data.message || '리뷰 제출에 실패했습니다.');
            }
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('리뷰 제출 중 오류가 발생했습니다.');
        });
    }
}