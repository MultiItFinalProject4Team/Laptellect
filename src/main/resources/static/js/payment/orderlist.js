let currentPage = 1;
const itemsPerPage = 3;
let filteredOrders = [];
let totalItems = 0;

function cancelOrder(impUid, amount) {
    if (confirm('정말로 주문을 취소하시겠습니까?')) {
        fetch('/payment/cancel', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                impUid: impUid,
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

function openReviewModal(productName, username, impUid) {
    document.getElementById('reviewModal').style.display = 'block';
    document.getElementById('reviewProductName').textContent = productName;
    document.getElementById('reviewUsername').textContent = username;
    document.getElementById('reviewModal').dataset.impuid = impUid;
}

function closeReviewModal() {
    document.getElementById('reviewModal').style.display = 'none';
}

function submitReview() {
    const productName = document.getElementById('reviewProductName').textContent;
    const username = document.getElementById('reviewUsername').textContent;
    const rating = document.getElementById('reviewRating').value;
    const content = document.getElementById('reviewContent').value;
    const impUid = document.getElementById('reviewModal').dataset.impuid;  // 저장된 impUid 가져오기

    fetch('/payment/reviews', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            productName: productName,
            username: username,
            rating: rating,
            content: content,
            impUid: impUid  // impUid 추가
        }),
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            closeReviewModal();
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

    // 리뷰 작성 버튼에 이벤트 리스너 추가
    document.querySelectorAll('.order-actions button:nth-child(2)').forEach(button => {
        button.addEventListener('click', function() {
            const productName = this.getAttribute('data-product-name');
            const username = this.getAttribute('data-username');
            const impUid = this.getAttribute('data-imp-uid');
            openReviewModal(productName, username, impUid);
        });
    });
});