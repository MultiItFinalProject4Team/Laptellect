let currentPage = 1;
const itemsPerPage = 12;
let filteredOrders = [];
let currentOrderId = null;

function formatPrice(price) {
  return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + '원';
}

function renderTable() {
  const tableBody = document.getElementById('orderTableBody');
  const ordersToShow = filteredOrders.length > 0 ? filteredOrders : orders;
  const start = (currentPage - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  const pageOrders = ordersToShow.slice(start, end);

  tableBody.innerHTML = '';

  if (ordersToShow.length === 0) {
    showNoResultsMessage();
    return;
  }

  pageOrders.forEach(order => {
    const row = `
      <tr>
        <td class="checkbox-column"><input type="checkbox" name="orderCheck" value="${order.imPortId}" ${order.refund === 'Y' ? 'disabled' : ''}></td>
        <td class="order-number-column"><span class="order-content" onclick="openModal(${order.payment_no})">${order.payment_no}</td>
        <td class="username-column">${order.username}</td>
        <td class="product-name-column">${order.productName}</td>
        <td class="price-column">${formatPrice(order.productPrice)}</td>
        <td class="purchase-price-column">${formatPrice(order.purchasePrice)}</td>
        <td class="date-column">${order.date_created}</td>
        <td class="imPortId-column">${order.imPortId}</td>
        <td class="refund-column">${order.refund}</td>
        <td class="refund-date-column">${order.refund_date || '환불되지 않음'}</td>
      </tr>
    `;
    tableBody.innerHTML += row;
  });
}

function showNoResultsMessage() {
  const tableBody = document.getElementById('orderTableBody');
  tableBody.innerHTML = `
    <tr>
      <td colspan="11" style="text-align: center; padding: 20px;">검색된 결과가 없습니다.</td>
    </tr>
  `;
}

function renderPagination() {
  const totalOrders = filteredOrders.length > 0 ? filteredOrders.length : orders.length;
  const totalPages = Math.ceil(totalOrders / itemsPerPage);
  const pageNumbers = document.getElementById('pageNumbers');
  pageNumbers.innerHTML = '';

  if (totalOrders === 0) {
    document.getElementById('prevButton').disabled = true;
    document.getElementById('nextButton').disabled = true;
    return;
  }

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
  const totalOrders = filteredOrders.length > 0 ? filteredOrders.length : orders.length;
  const totalPages = Math.ceil(totalOrders / itemsPerPage);

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

function searchOrders() {
  const searchCategory = document.getElementById('searchCategory').value;
  const searchTerm = document.getElementById('searchInput').value.toLowerCase();

  filteredOrders = orders.filter(order => {
    if (searchCategory === 'all') {
      return Object.values(order).some(value =>
        value && value.toString().toLowerCase().includes(searchTerm)
      );
    } else {
      const value = order[searchCategory];
      return value && value.toString().toLowerCase().includes(searchTerm);
    }
  });

  currentPage = 1;

  if (filteredOrders.length === 0) {
    showNoResultsMessage();
    renderPagination();
  } else {
    renderTable();
    renderPagination();
  }
}

function refundSelectedOrders() {
  const selectedOrders = Array.from(document.getElementsByName('orderCheck'))
    .filter(checkbox => checkbox.checked && !checkbox.disabled)
    .map(checkbox => ({
      imPortId: checkbox.value,
      amount: orders.find(order => order.imPortId === checkbox.value).purchasePrice
    }));

  if (selectedOrders.length === 0) {
    alert('환불할 주문을 선택해주세요.');
    return;
  }

  if (confirm('선택한 주문을 환불하시겠습니까?')) {
    Promise.all(selectedOrders.map(order =>
      fetch('/payment/cancel', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(order),
      }).then(response => response.json())
    ))
    .then(results => {
      const successCount = results.filter(result => result.success).length;
      const failCount = results.length - successCount;

      alert(`환불 처리 완료:\n성공: ${successCount}건\n실패: ${failCount}건`);
      location.reload();
    })
    .catch(error => {
      console.error('Error:', error);
      alert('주문 환불 중 오류가 발생했습니다.');
    });
  }
}

function openModal(orderId) {
  currentOrderId = orderId;
  const order = orders.find(o => o.payment_no === orderId);
  if (order) {
    document.getElementById('modalOrderNumber').textContent = order.payment_no;
    document.getElementById('modalUsername').textContent = order.username;
    document.getElementById('modalProductName').textContent = order.productName;
    document.getElementById('modalProductPrice').textContent = formatPrice(order.productPrice);
    document.getElementById('modalPurchasePrice').textContent = formatPrice(order.purchasePrice);
    document.getElementById('modalOrderDate').textContent = order.date_created;
    document.getElementById('modalimPortId').textContent = order.imPortId;
    document.getElementById('modalRefundStatus').textContent = order.refund;
    document.getElementById('modalRefundDate').textContent = order.refund_date || '환불되지 않음';

    const modalRefundButton = document.getElementById('modalRefundButton');
    if (order.refund === 'N') {
      modalRefundButton.style.display = 'block';
      modalRefundButton.disabled = false;
      modalRefundButton.onclick = () => refundSingleOrder(order.imPortId, order.purchasePrice);
    } else {
      modalRefundButton.style.display = 'none';
    }

    document.getElementById('orderModal').style.display = 'block';
  }
}

function refundSingleOrder(imPortId, amount) {
  if (confirm('이 주문을 환불하시겠습니까?')) {
    fetch('/payment/cancel', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ imPortId, amount }),
    })
    .then(response => response.json())
    .then(data => {
      if (data.success) {
        alert('주문이 성공적으로 환불되었습니다.');
        location.reload();
      } else {
        alert('주문 환불 중 오류가 발생했습니다: ' + data.message);
      }
    })
    .catch(error => {
      console.error('Error:', error);
      alert('주문 환불 중 오류가 발생했습니다.');
    });
  }
}

function closeModal() {
  document.getElementById('orderModal').style.display = 'none';
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
  renderTable();
  renderPagination();

  // 이벤트 리스너 설정
  document.querySelector('.close').addEventListener('click', closeModal);
  window.addEventListener('click', function(event) {
    if (event.target == document.getElementById('orderModal')) {
      closeModal();
    }
  });

  document.getElementById('selectAll').addEventListener('change', function() {
    const checkboxes = document.getElementsByName('orderCheck');
    checkboxes.forEach(checkbox => {
      if (!checkbox.disabled) {
        checkbox.checked = this.checked;
      }
    });
  });
});