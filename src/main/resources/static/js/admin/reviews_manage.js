let currentPage = 1;
const itemsPerPage = 12;
let filteredReviews = [];

function renderTable() {
  const tableBody = document.getElementById('reviewTableBody');
  const start = (currentPage - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  const pageReviews = (filteredReviews.length > 0 ? filteredReviews : reviews).slice(start, end);

  tableBody.innerHTML = '';
  pageReviews.forEach(review => {
    const row = `
      <tr>
        <td class="checkbox-column"><input type="checkbox" name="reviewCheck" value="${review.payment_product_reviews_no}"></td>
        <td class="review-number-column">${review.payment_product_reviews_no}</td>
        <td class="product-name-column">${review.product_name}</td>
        <td class="author-column">${review.username}</td>
        <td class="content-column">${review.content}</td>
        <td class="rating-column">${review.rating}점</td>
        <td class="date-column">${review.create_date}</td>
        <td class="date-column">${review.modify_date != null ? review.modify_date : '수정사항없음'}</td>
      </tr>
    `;
    tableBody.innerHTML += row;
  });
}

function renderPagination() {
  const totalReviews = filteredReviews.length > 0 ? filteredReviews.length : reviews.length;
  const totalPages = Math.ceil(totalReviews / itemsPerPage);
  const pageNumbers = document.getElementById('pageNumbers');
  pageNumbers.innerHTML = '';

  for (let i = 1; i <= totalPages; i++) {
    const button = document.createElement('button');
    button.textContent = i;
    button.onclick = () => changePage(i);
    if (i === currentPage) {
      button.classList.add('active');
    }
    pageNumbers.appendChild(button);
  }

  document.getElementById('prevButton').disabled = currentPage === 1;
  document.getElementById('nextButton').disabled = currentPage === totalPages;
}

function changePage(page) {
  const totalReviews = filteredReviews.length > 0 ? filteredReviews.length : reviews.length;
  const totalPages = Math.ceil(totalReviews / itemsPerPage);

  if (page === 'prev' && currentPage > 1) {
    currentPage--;
  } else if (page === 'next' && currentPage < totalPages) {
    currentPage++;
  } else if (typeof page === 'number') {
    currentPage = page;
  }

  renderTable();
  renderPagination();
}

function searchReviews() {
  const searchTerm = document.getElementById('searchInput').value.toLowerCase();
  filteredReviews = reviews.filter(review =>
    review.product_name.toLowerCase().includes(searchTerm) ||
    review.username.toLowerCase().includes(searchTerm) ||
    review.content.toLowerCase().includes(searchTerm)
  );
  currentPage = 1;
  renderTable();
  renderPagination();
}

function deleteSelectedReviews() {
  const selectedReviews = Array.from(document.getElementsByName('reviewCheck'))
    .filter(checkbox => checkbox.checked)
    .map(checkbox => checkbox.value);

  if (selectedReviews.length === 0) {
    alert('삭제할 리뷰를 선택해주세요.');
    return;
  }

  if (confirm('선택한 리뷰를 삭제하시겠습니까?')) {
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
        alert('선택한 리뷰가 삭제되었습니다.');
        // 삭제 후 리뷰 목록 새로고침
        location.reload();
      } else {
        alert('리뷰 삭제 중 오류가 발생했습니다.');
      }
    })
    .catch(error => {
      console.error('Error:', error);
      alert('리뷰 삭제 중 오류가 발생했습니다.');
    });
  }
}

// 초기 렌더링
renderTable();
renderPagination();

// Select all checkbox functionality
document.getElementById('selectAll').addEventListener('change', function() {
  const checkboxes = document.getElementsByName('reviewCheck');
  checkboxes.forEach(checkbox => checkbox.checked = this.checked);
});