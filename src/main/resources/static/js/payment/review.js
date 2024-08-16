// DOM이 로드된 후 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', function() {
    const reviewModal = new bootstrap.Modal(document.getElementById('reviewModal'));
    const reviewContent = document.getElementById('reviewContent');
    const submitReviewButton = document.getElementById('submitReviewButton');
    const reviewModalLabel = document.getElementById('reviewModalLabel');

    // 리뷰 내용 입력 이벤트 리스너
    reviewContent.addEventListener('input', function() {
        const isEmpty = this.value.trim() === '';
        submitReviewButton.disabled = isEmpty;
    });

    // 리뷰 모달 열기 버튼 이벤트 리스너
    const reviewModalButton = document.getElementById('reviewModalButton');
    if (reviewModalButton) {
        reviewModalButton.addEventListener('click', function() {
            resetModal();
            reviewModal.show();
        });
    }

    // 리뷰 제출 버튼 이벤트 리스너
    submitReviewButton.addEventListener('click', function() {
        const paymentProductReviewsNo = document.getElementById('paymentProductReviewsNo')?.value;
        if (paymentProductReviewsNo) {
            updateReview(paymentProductReviewsNo);
        } else {
            submitReview();
        }
    });

    // 리뷰 수정 버튼 이벤트 리스너
    document.querySelectorAll('.edit-review').forEach(button => {
        button.addEventListener('click', function() {
            const paymentProductReviewsNo = this.getAttribute('data-review-id');
            const content = this.getAttribute('data-review-content');
            const rating = this.getAttribute('data-review-rating');

            document.getElementById('reviewContent').value = content;
            document.getElementById('reviewRating').value = rating;

            const reviewIdField = document.getElementById('paymentProductReviewsNo') || document.createElement('input');
            reviewIdField.type = 'hidden';
            reviewIdField.id = 'paymentProductReviewsNo';
            reviewIdField.value = paymentProductReviewsNo;
            document.getElementById('reviewModal').querySelector('.modal-body').appendChild(reviewIdField);

            reviewModalLabel.textContent = '리뷰 수정하기';
            submitReviewButton.textContent = '리뷰 수정';
            submitReviewButton.disabled = false;

            reviewModal.show();
        });
    });

    // 리뷰 삭제 버튼 이벤트 리스너
    document.querySelectorAll('.delete-review').forEach(button => {
        button.addEventListener('click', function() {
            const paymentProductReviewsNo = this.getAttribute('data-review-id');
            if (confirm('정말로 이 리뷰를 삭제하시겠습니까?')) {
                deleteReview(paymentProductReviewsNo);
            }
        });
    });

    // 페이징 기능 초기화
    initPagination();
});

// 모달 리셋 함수
function resetModal() {
    document.getElementById('reviewContent').value = '';
    document.getElementById('reviewRating').value = '5';
    document.getElementById('reviewModalLabel').textContent = '리뷰 작성하기';
    document.getElementById('submitReviewButton').textContent = '리뷰 제출';
    document.getElementById('submitReviewButton').disabled = true;
    const reviewIdField = document.getElementById('paymentProductReviewsNo');
    if (reviewIdField) reviewIdField.remove();
}

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

    const reviewData = {
        memberNo: memberNo,
        productNo: productNo,
        content: content,
        rating: rating,
        imPortId: imPortId
    };

    if (confirm("리뷰를 작성하게 되면 주문 취소가 불가능해집니다. \n리뷰를 등록하시겠습니까?")) {
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
                alert('리뷰가 성공적으로 등록되었습니다.');
                bootstrap.Modal.getInstance(document.getElementById('reviewModal')).hide();
                window.location.reload();
            } else {
                alert(data.message || '리뷰 제출에 실패했습니다.');
            }
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('리뷰 제출 중 오류가 발생했습니다.');
        });
    }
}

// 리뷰 수정 함수
function updateReview(paymentProductReviewsNo) {
    const content = document.getElementById('reviewContent').value;
    const rating = document.getElementById('reviewRating').value;

    const reviewData = {
        paymentProductReviewsNo: paymentProductReviewsNo,
        content: content,
        rating: rating
    };

    fetch('/payment/reviews/update', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(reviewData),
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('리뷰가 성공적으로 수정되었습니다.');
            bootstrap.Modal.getInstance(document.getElementById('reviewModal')).hide();
            window.location.reload();
        } else {
            alert(data.message || '리뷰 수정에 실패했습니다.');
        }
    })
    .catch((error) => {
        console.error('Error:', error);
        alert('리뷰 수정 중 오류가 발생했습니다.');
    });
}

// 리뷰 삭제 함수
function deleteReview(paymentProductReviewsNo) {
    fetch('/payment/reviews/delete', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ paymentProductReviewsNo: paymentProductReviewsNo }),
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('리뷰가 성공적으로 삭제되었습니다.');
            window.location.reload();
        } else {
            alert(data.message || '리뷰 삭제에 실패했습니다.');
        }
    })
    .catch((error) => {
        console.error('Error:', error);
        alert('리뷰 삭제 중 오류가 발생했습니다.');
    });
}

// 페이징 관련 변수
const itemsPerPage = 10;
let currentPage = 1;
let reviewItems;

// 페이징 초기화 함수
function initPagination() {
    reviewItems = document.querySelectorAll('.review-item');
    const totalPages = Math.ceil(reviewItems.length / itemsPerPage);

    showPage(currentPage);
    setupPagination(totalPages);
}

// 특정 페이지 표시 함수
function showPage(page) {
    const startIndex = (page - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;

    reviewItems.forEach((item, index) => {
        if (index >= startIndex && index < endIndex) {
            item.style.display = '';
        } else {
            item.style.display = 'none';
        }
    });
}

// 페이지네이션 설정 함수
function setupPagination(totalPages) {
    const paginationElement = document.getElementById('reviewPagination');
    paginationElement.innerHTML = '';

    // 이전 버튼
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
    prevLi.innerHTML = `<a class="page-link prev_btn" href="#">이전</a>`;
    paginationElement.appendChild(prevLi);

    // 페이지 번호
    for (let i = 1; i <= totalPages; i++) {
        const li = document.createElement('li');
        li.className = `page-item ${i === currentPage ? 'active' : ''}`;
        li.innerHTML = `<a class="page-link" href="#" data-page="${i}">${i}</a>`;
        paginationElement.appendChild(li);
    }

    // 다음 버튼
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
    nextLi.innerHTML = `<a class="page-link next_btn" href="#">다음</a>`;
    paginationElement.appendChild(nextLi);

    paginationElement.addEventListener('click', function(e) {
        e.preventDefault();
        if (e.target.tagName === 'A') {
            if (e.target.classList.contains('prev_btn') && currentPage > 1) {
                currentPage--;
            } else if (e.target.classList.contains('next_btn') && currentPage < totalPages) {
                currentPage++;
            } else if (!e.target.classList.contains('prev_btn') && !e.target.classList.contains('next_btn')) {
                currentPage = parseInt(e.target.getAttribute('data-page'));
            }
            showPage(currentPage);
            updatePaginationActive();
        }
    });
}

// 활성 페이지 업데이트 함수
function updatePaginationActive() {
    const paginationItems = document.querySelectorAll('#reviewPagination .page-item');
    paginationItems.forEach((item, index) => {
        if (index === 0) {
            item.classList.toggle('disabled', currentPage === 1);
        } else if (index === paginationItems.length - 1) {
            item.classList.toggle('disabled', currentPage === paginationItems.length - 2);
        } else {
            item.classList.toggle('active', index === currentPage);
        }
    });
}