let currentPage = 1;
const itemsPerPage = 12;
let filteredReviews = [];
let currentReviewId = null;

function formatDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function renderTable() {
  const tableBody = document.getElementById('reviewTableBody');
  const reviewsToShow = filteredReviews.length > 0 ? filteredReviews : reviews;
  const start = (currentPage - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  const pageReviews = reviewsToShow.slice(start, end);

  tableBody.innerHTML = '';

  if (reviewsToShow.length === 0) {
    showNoResultsMessage();
    return;
  }

  pageReviews.forEach(review => {
    const createdAtFormatted = formatDate(new Date(review.createdAt));
    const modifyAtFormatted = review.modifyAt != null ? formatDate(new Date(review.modifyAt)) : '수정사항없음';

    const row = `
      <tr>
        <td class="checkbox-column"><input type="checkbox" name="reviewCheck" value="${review.paymentProductReviewsNo}"></td>
        <td class="review-number-column"><span class="review-content" onclick="openModal(${review.paymentProductReviewsNo})">${review.paymentProductReviewsNo}</td>
        <td class="product-name-column"><a href="/product/productDetail?productNo=${review.productNo}" class="review-content">${review.productName}</a></td>
        <td class="author-column">${review.memberName}</td>
        <td class="content-column">${review.content}</span></td>
        <td class="rating-column">${review.rating}점</td>
        <td class="date-column">${createdAtFormatted}</td>
        <td class="date-column">${modifyAtFormatted}</td>
      </tr>
    `;
    tableBody.innerHTML += row;
  });
}

function showNoResultsMessage() {
  const tableBody = document.getElementById('reviewTableBody');
  tableBody.innerHTML = `
    <tr>
      <td colspan="8" style="text-align: center; padding: 20px;">검색된 내역이 없습니다.</td>
    </tr>
  `;
}

function renderPagination() {
  const totalPages = Math.ceil((filteredReviews.length > 0 ? filteredReviews.length : reviews.length) / itemsPerPage);
  const pageNumbers = document.getElementById('pageNumbers');
  pageNumbers.innerHTML = '';

  let startPage = Math.max(currentPage - 2, 1);
  let endPage = Math.min(startPage + 4, totalPages);

  if (endPage - startPage < 4) {
    startPage = Math.max(endPage - 4, 1);
  }

  for (let i = startPage; i <= endPage; i++) {
    const pageNumber = document.createElement('button');
    pageNumber.textContent = i;
    pageNumber.classList.add('page-number');
    if (i === currentPage) {
      pageNumber.classList.add('active');
    }
    pageNumber.onclick = () => changePage(i);
    pageNumbers.appendChild(pageNumber);
  }

  document.getElementById('prevButton').disabled = currentPage === 1;
  document.getElementById('nextButton').disabled = currentPage === totalPages;
}

function changePage(page) {
  if (typeof page === 'number') {
    currentPage = page;
  } else if (page === 'prev' && currentPage > 1) {
    currentPage--;
  } else if (page === 'next' && currentPage < Math.ceil(reviews.length / itemsPerPage)) {
    currentPage++;
  }
  renderTable();
  renderPagination();
}

function searchReviews() {
  const searchCategory = document.getElementById('searchCategory').value;
  const searchTerm = document.getElementById('searchInput').value.toLowerCase();

  filteredReviews = reviews.filter(review => {
    if (searchCategory === 'all') {
      return Object.values(review).some(value =>
        value && value.toString().toLowerCase().includes(searchTerm)
      );
    } else {
      const value = review[searchCategory];
      return value && value.toString().toLowerCase().includes(searchTerm);
    }
  });

  currentPage = 1;

  if (filteredReviews.length === 0) {
    showNoResultsMessage();
    renderPagination();
  } else {
    renderTable();
    renderPagination();
  }
}

function deleteSelectedReviews() {
  const selectedReviews = Array.from(document.getElementsByName('reviewCheck'))
    .filter(checkbox => checkbox.checked)
    .map(checkbox => checkbox.value);

  if (selectedReviews.length === 0) {
    swal('삭제할 리뷰를 선택해주세요.', '', 'info');
    return;
  }

  swal({
    title: '선택한 리뷰를 삭제하시겠습니까?',
    text: "이 작업은 되돌릴 수 없습니다!",
    type: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: '예, 삭제합니다!',
    cancelButtonText: '취소'
  }).then(function(isConfirm) {
    if (isConfirm) {
      fetch('/admin/deleteReviews', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(selectedReviews),
      })
      .then(response => response.json())
      .then(data => {
        if (data.success) {
          swal('삭제 완료', '선택한 리뷰가 삭제되었습니다.', 'success')
            .then(() => {
              location.reload();
            });
        } else {
          swal('오류', '리뷰 삭제 중 오류가 발생했습니다.', 'error');
        }
      })
      .catch(error => {
        console.error('Error:', error);
        swal('오류', '리뷰 삭제 중 오류가 발생했습니다.', 'error');
      });
    }
  });
}

function openModal(reviewId) {
  currentReviewId = reviewId;
  const review = reviews.find(r => r.paymentProductReviewsNo === reviewId);
  if (review) {
    document.getElementById('modalReviewNumber').textContent = review.paymentProductReviewsNo;
    document.getElementById('modalAuthor').textContent = review.memberName;
    document.getElementById('modalProductName').textContent = review.productName;
    document.getElementById('modalRating').textContent = review.rating + '점';
    document.getElementById('modalContent').textContent = review.content;
    document.getElementById('modalCreateDate').textContent = review.createdAt;
    document.getElementById('modalModifyDate').textContent = review.modifyAt != null ? review.modifyAt : '수정사항없음';

    document.getElementById('reviewModal').style.display = 'block';
  }
}

function closeModal() {
  document.getElementById('reviewModal').style.display = 'none';
}

function deleteSingleReview() {
  if (currentReviewId) {
    swal({
      title: '이 리뷰를 삭제하시겠습니까?',
      text: "이 작업은 되돌릴 수 없습니다!",
      type: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: '예, 삭제합니다!',
      cancelButtonText: '취소'
    }).then(function(isConfirm) {
      if (isConfirm) {
        fetch('/admin/deleteReviews', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify([currentReviewId]),
        })
        .then(response => response.json())
        .then(data => {
          if (data.success) {
            swal('삭제 완료', '리뷰가 삭제되었습니다.', 'success')
              .then(() => {
                closeModal();
                location.reload();
              });
          } else {
            swal('오류', '리뷰 삭제 중 오류가 발생했습니다.', 'error');
          }
        })
        .catch(error => {
          console.error('Error:', error);
          swal('오류', '리뷰 삭제 중 오류가 발생했습니다.', 'error');
        });
      }
    });
  }
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
  renderTable();
  renderPagination();

  // 이벤트 리스너 설정
  document.querySelector('.close').addEventListener('click', closeModal);
  window.addEventListener('click', function(event) {
    if (event.target == document.getElementById('reviewModal')) {
      closeModal();
    }
  });

  document.getElementById('selectAll').addEventListener('change', function() {
    const checkboxes = document.getElementsByName('reviewCheck');
    checkboxes.forEach(checkbox => checkbox.checked = this.checked);
  });

  document.getElementById('modalDeleteButton').addEventListener('click', deleteSingleReview);
});