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
        <td class="checkbox-column"><input type="checkbox" name="orderCheck" value="${order.imPortId}" data-amount="${order.purchasePrice}" data-payment-no="${order.paymentNo}" ${order.refund === 'Y' ? 'disabled' : ''}></td>
        <td class="order-number-column"><span class="order-content" onclick="openModal(${order.paymentNo})">${order.paymentNo}</span></td>
        <td class="username-column">${order.userName}</td>
        <td class="product-name-column">${order.productName}</td>
        <td class="price-column">${formatPrice(order.productPrice)}</td>
        <td class="purchase-price-column">${formatPrice(order.purchasePrice)}</td>
        <td class="date-column">${order.createdAt}</td>
        <td class="imPortId-column">${order.imPortId}</td>
        <td class="refund-column">${order.refund}</td>
        <td class="refund-date-column">${order.refundAt || '환불되지 않음'}</td>
      </tr>
    `;
    tableBody.innerHTML += row;
  });
}

function showNoResultsMessage() {
  const tableBody = document.getElementById('orderTableBody');
  tableBody.innerHTML = `
    <tr>
      <td colspan="10" style="text-align: center; padding: 20px;">검색된 결과가 없습니다.</td>
    </tr>
  `;
}

function renderPagination() {
  const totalPages = Math.ceil((filteredOrders.length > 0 ? filteredOrders.length : orders.length) / itemsPerPage);
  const pageNumbers = document.getElementById('pageNumbers');
  pageNumbers.innerHTML = '';

  for (let i = 1; i <= totalPages; i++) {
    const pageNumber = document.createElement('button');
    pageNumber.textContent = i;
    pageNumber.onclick = () => changePage(i);
    if (i === currentPage) {
      pageNumber.classList.add('active');
    }
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
  } else if (page === 'next' && currentPage < Math.ceil((filteredOrders.length > 0 ? filteredOrders.length : orders.length) / itemsPerPage)) {
    currentPage++;
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
  renderTable();
  renderPagination();
}

function refundSelectedOrders() {
  const selectedOrders = Array.from(document.querySelectorAll('input[name="orderCheck"]:checked'))
    .map(checkbox => ({
      imPortId: checkbox.value,
      amount: parseFloat(checkbox.getAttribute('data-amount')),
      paymentNo: parseInt(checkbox.getAttribute('data-payment-no'))
    }));

  if (selectedOrders.length === 0) {
    alert('환불할 주문을 선택해주세요.');
    return;
  }

  if (confirm(`선택한 ${selectedOrders.length}개의 주문을 환불하시겠습니까?`)) {
    Promise.all(selectedOrders.map(order => refundOrder(order.imPortId, order.amount, order.paymentNo)))
      .then(results => {
        const successCount = results.filter(result => result.success).length;
        const failCount = results.length - successCount;

        if (successCount > 0) {
          alert(`${successCount}개의 주문이 성공적으로 환불되었습니다.${failCount > 0 ? `\n${failCount}개의 주문 환불에 실패했습니다.` : ''}`);
          location.reload();
        } else {
          alert('환불 처리 중 오류가 발생했습니다.');
        }
      })
      .catch(error => {
        console.error('Error:', error);
        alert('환불 처리 중 오류가 발생했습니다.');
      });
  }
}

function openModal(orderId) {
  currentOrderId = orderId;
  const order = orders.find(o => o.paymentNo === orderId);
  if (order) {
    document.getElementById('modalOrderNumber').textContent = order.paymentNo;
    document.getElementById('modalUsername').textContent = order.userName;
    document.getElementById('modalProductName').textContent = order.productName;
    document.getElementById('modalProductPrice').textContent = formatPrice(order.productPrice);
    document.getElementById('modalPurchasePrice').textContent = formatPrice(order.purchasePrice);
    document.getElementById('modalOrderDate').textContent = order.createdAt;
    document.getElementById('modalimPortId').textContent = order.imPortId;
    document.getElementById('modalRefundStatus').textContent = order.refund;
    document.getElementById('modalRefundDate').textContent = order.refundAt || '환불되지 않음';

    const modalRefundButton = document.getElementById('modalRefundButton');
    if (order.refund === 'N') {
      modalRefundButton.style.display = 'block';
      modalRefundButton.disabled = false;
      modalRefundButton.onclick = () => refundSingleOrder(order.imPortId, order.purchasePrice, order.paymentNo);
    } else {
      modalRefundButton.style.display = 'none';
    }

    document.getElementById('orderModal').style.display = 'block';
  }
}

function refundSingleOrder(imPortId, amount, paymentNo) {
  if (confirm('이 주문을 환불하시겠습니까?')) {
    refundOrder(imPortId, amount, paymentNo)
      .then(result => {
        if (result.success) {
          alert('주문이 성공적으로 환불되었습니다.');
          location.reload();
        } else {
          alert('환불 처리 중 오류가 발생했습니다: ' + result.message);
        }
      })
      .catch(error => {
        console.error('Error:', error);
        alert('환불 처리 중 오류가 발생했습니다.');
      });
  }
}

function refundOrder(imPortId, amount, paymentNo) {
  return fetch('/payment/cancel', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      imPortId: imPortId,
      amount: amount,
      paymentNo: paymentNo
    }),
  })
  .then(response => response.json())
  .then(data => {
    if (data.success) {
      return { success: true };
    } else {
      return { success: false, message: data.message };
    }
  })
  .catch(error => {
    console.error('Error:', error);
    return { success: false, message: '환불 처리 중 오류가 발생했습니다.' };
  });
}

function closeModal() {
  document.getElementById('orderModal').style.display = 'none';
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
  console.log('Orders data:', orders); // 디버깅을 위한 로그 추가
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

  // 환불 버튼 활성화 여부 체크
  document.addEventListener('change', function(event) {
    if (event.target.name === 'orderCheck' || event.target.id === 'selectAll') {
      const checkedOrders = document.querySelectorAll('input[name="orderCheck"]:checked:not(:disabled)');
      const refundSelectedButton = document.getElementById('refundSelectedButton');
      if (refundSelectedButton) {
        refundSelectedButton.disabled = checkedOrders.length === 0;
      }
    }
  });
});