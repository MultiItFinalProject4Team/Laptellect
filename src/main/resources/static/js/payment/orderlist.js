let currentPage = 1;
const itemsPerPage = 3;
let filteredOrders = [];
let totalItems = 0;

function cancelOrder(imPortId, amount) {
    if (confirm('정말로 주문을 취소하시겠습니까?')) {
        fetch('/payment/cancel', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                imPortId: imPortId,
                amount: parseFloat(amount)
            }),
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                location.reload();
            } else {
                alert(data.message || '주문 취소에 실패했습니다.');
            }
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('주문 취소 중 오류가 발생했습니다.');
        });
    }
}

function updateTotalItemsCount() {
    totalItems = filteredOrders.length > 0 ? filteredOrders.length : document.getElementsByClassName('order-item').length;
    document.getElementById('totalItemsCount').textContent = totalItems;
}

function searchOrders() {
    const searchText = document.getElementById('searchInput').value.toLowerCase();
    const orderItems = document.getElementsByClassName('order-item');
    filteredOrders = [];

    for (let item of orderItems) {
        const productName = item.querySelector('h3').textContent.toLowerCase();
        if (productName.includes(searchText)) {
            filteredOrders.push(item);
            item.style.display = '';
        } else {
            item.style.display = 'none';
        }
    }

    currentPage = 1;
    updateTotalItemsCount();
    displayPage(currentPage);
    updatePagination();
}

function displayPage(page) {
    const startIndex = (page - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const orderItems = filteredOrders.length > 0 ? filteredOrders : document.getElementsByClassName('order-item');

    for (let i = 0; i < orderItems.length; i++) {
        if (i >= startIndex && i < endIndex) {
            orderItems[i].style.display = '';
        } else {
            orderItems[i].style.display = 'none';
        }
    }
}

function updatePagination() {
    const totalItems = filteredOrders.length > 0 ? filteredOrders.length : document.getElementsByClassName('order-item').length;
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const paginationElement = document.querySelector('.pagination');
    paginationElement.innerHTML = '';

    // 이전 버튼
    const prevButton = document.createElement('button');
    prevButton.textContent = '< 이전';
    prevButton.onclick = () => {
        if (currentPage > 1) {
            currentPage--;
            displayPage(currentPage);
            updatePagination();
        }
    };
    paginationElement.appendChild(prevButton);

    // 페이지 숫자
    for (let i = 1; i <= totalPages; i++) {
        const pageButton = document.createElement('button');
        pageButton.textContent = i;
        pageButton.onclick = () => {
            currentPage = i;
            displayPage(currentPage);
            updatePagination();
        };
        if (i === currentPage) {
            pageButton.classList.add('active');
        }
        paginationElement.appendChild(pageButton);
    }

    // 다음 버튼
    const nextButton = document.createElement('button');
    nextButton.textContent = '다음 >';
    nextButton.onclick = () => {
        if (currentPage < totalPages) {
            currentPage++;
            displayPage(currentPage);
            updatePagination();
        }
    };
    paginationElement.appendChild(nextButton);
}

function openReviewModal(productName, userName, imPortId) {
    document.getElementById('reviewModal').style.display = 'block';
    document.getElementById('reviewProductName').textContent = productName;
    document.getElementById('reviewUsername').textContent = userName;
    document.getElementById('reviewModal').dataset.imPortId = imPortId;

    // 리뷰 내용 입력 필드와 제출 버튼 초기화
    const reviewContent = document.getElementById('reviewContent');
    const submitReviewButton = document.getElementById('submitReviewButton');
    reviewContent.value = '';
    submitReviewButton.disabled = true;
    submitReviewButton.classList.add('disabled');  // 클래스 추가

    // 리뷰 내용 입력 이벤트 리스너 추가
    reviewContent.addEventListener('input', function() {
        const isEmpty = this.value.trim() === '';
        submitReviewButton.disabled = isEmpty;
        if (isEmpty) {
            submitReviewButton.classList.add('disabled');
        } else {
            submitReviewButton.classList.remove('disabled');
        }
    });
}

function closeReviewModal() {
    document.getElementById('reviewModal').style.display = 'none';
}

function submitReview() {
    if (confirm("리뷰를 작성하게 되면 주문 취소가 불가능해집니다. \n리뷰를 등록하시겠습니까?")) {
        const productName = document.getElementById('reviewProductName').textContent;
        const userName = document.getElementById('reviewUsername').textContent;
        const rating = document.getElementById('reviewRating').value;
        const content = document.getElementById('reviewContent').value;
        const imPortId = document.getElementById('reviewModal').dataset.imPortId;

        fetch('/payment/reviews', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                productName: productName,
                userName: userName,
                rating: rating,
                content: content,
                imPortId: imPortId
            }),
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                closeReviewModal();

                // 리뷰 버튼과 취소 버튼 상태 업데이트
                const reviewButton = document.querySelector(`button.review-button[data-imp-uid="${imPortId}"]`);
                const cancelButton = document.querySelector(`button.cancel-button[data-imp-uid="${imPortId}"]`);
                if (reviewButton) {
                    reviewButton.classList.add('reviewed');
                    reviewButton.disabled = true;
                    reviewButton.onclick = null;
                }
                if (cancelButton) {
                    cancelButton.classList.add('disabled');
                    cancelButton.disabled = true;
                    cancelButton.onclick = null;
                }

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

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('searchButton').addEventListener('click', searchOrders);
    document.getElementById('searchInput').addEventListener('keyup', function(event) {
        if (event.key === 'Enter') {
            searchOrders();
        }
    });

    updateTotalItemsCount();
    displayPage(currentPage);
    updatePagination();

    document.querySelector('.close').addEventListener('click', closeReviewModal);

    window.onclick = function(event) {
        if (event.target == document.getElementById('reviewModal')) {
            closeReviewModal();
        }
    }

    // 리뷰 내용 입력 필드에 이벤트 리스너 추가
    document.getElementById('reviewContent').addEventListener('input', function() {
        const submitReviewButton = document.getElementById('submitReviewButton');
        const isEmpty = this.value.trim() === '';
        submitReviewButton.disabled = isEmpty;
        if (isEmpty) {
            submitReviewButton.classList.add('disabled');
        } else {
            submitReviewButton.classList.remove('disabled');
        }
    });
});

// CSS 스타일 추가
const style = document.createElement('style');
style.textContent = `
    .review-button.reviewed,
    .cancel-button.disabled,
    #submitReviewButton.disabled {
        background-color: #cccccc;
        color: #666666;
        cursor: not-allowed;
    }
`;
document.head.appendChild(style);