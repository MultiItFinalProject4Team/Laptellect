// 전역 변수 선언
let userName, productName, productPrice, originalPrice, possessionPoint;

// 숫자 포맷팅 함수
function formatNumber(num) {
    return num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,');
}

// DOM이 로드된 후 실행될 함수
document.addEventListener('DOMContentLoaded', function() {
    // Thymeleaf에서 전달된 데이터를 JavaScript 변수에 할당
    userName = document.getElementById('name').value;
    productName = document.querySelector('.product-info-table tbody tr td:nth-child(2)').textContent;
    originalPrice = parseInt(document.querySelector('.price').textContent.replace(/[^\d]/g, ''));
    possessionPoint = parseInt(document.querySelector('.payment-price-row:nth-child(2) span:last-child').textContent.replace(/[^\d]/g, ''));

    // 초기 가격 설정
    document.getElementById('originalPrice').textContent = formatNumber(originalPrice) + '원';
    document.getElementById('amount').textContent = formatNumber(originalPrice) + '원';

    // 초기 총 결제금액 설정
    updateTotalPrice();

    // 이벤트 리스너 추가
    document.querySelector('.quantity-minus').addEventListener('click', decreaseQuantity);
    document.querySelector('.quantity-plus').addEventListener('click', increaseQuantity);
    document.getElementById('pointInput').addEventListener('keyup', updateTotalPrice);
});

function decreaseQuantity(event) {
    event.preventDefault();
    let quantity = parseInt(document.getElementById('productQuantity').textContent);
    if (quantity > 1) {
        document.getElementById('productQuantity').textContent = quantity - 1;
        updatePriceDisplay();
        updateTotalPrice();
    }
}

function increaseQuantity(event) {
    event.preventDefault();
    let quantity = parseInt(document.getElementById('productQuantity').textContent);
    document.getElementById('productQuantity').textContent = quantity + 1;
    updatePriceDisplay();
    updateTotalPrice();
}

function updatePriceDisplay() {
    let quantity = parseInt(document.getElementById('productQuantity').textContent);
    let totalPrice = originalPrice * quantity;
    document.querySelector('.price').textContent = formatNumber(totalPrice) + '원';
    // 판매가 업데이트
    document.getElementById('originalPrice').textContent = formatNumber(totalPrice) + '원';
}

function updateTotalPrice() {
    const pointInput = document.getElementById('pointInput');
    const pointUsageDisplay = document.getElementById('pointUsageDisplay');
    const amountDisplay = document.getElementById('amount');

    let quantity = parseInt(document.getElementById('productQuantity').textContent);
    let pointValue = parseInt(pointInput.value.replace(/,/g, '')) || 0;

    // 입력된 포인트가 보유 포인트를 초과하지 않도록 제한
    if (pointValue > possessionPoint) {
        pointValue = possessionPoint;
        pointInput.value = formatNumber(pointValue);
    }

    let totalPrice = originalPrice * quantity - pointValue;

    // 총 결제금액이 100원 미만이 되지 않도록 제한
    if (totalPrice < 100) {
        alert("총 결제금액이 100원보다 작아질 수 없습니다");
        totalPrice = 100;
        pointValue = originalPrice * quantity - 100;
        pointInput.value = formatNumber(pointValue);
    }

    pointUsageDisplay.textContent = formatNumber(pointValue) + '원';
    amountDisplay.textContent = formatNumber(totalPrice) + '원';
}

function mypayment() {
    const myAmount = Number(document.getElementById("amount").textContent.replace(/[^\d]/g, ''));
    const usedPoints = Number(document.getElementById("pointInput").value.replace(/,/g, '')) || 0;
    const IMP = window.IMP;
    IMP.init("imp64527455");

    IMP.request_pay(
        {
            pg: "html5_inicis",
            pay_method: "card",
            name: productName,
            amount: myAmount,
            buyer_email: "",
            buyer_name: userName,
            buyer_tel: "010-4242-4242",
            buyer_addr: "서울특별시 강남구 신사동",
            buyer_postcode: "01181",
            m_redirect_url: "",
        },
        async (rsp) => {
            if (rsp.success) {
                try {
                    const { data } = await axios.post('/payment/verifyPayment', {
                        imPortId: rsp.imp_uid,
                        amount: myAmount,
                        usedPoints: usedPoints,
                        productName: productName
                    });

                    if (data.success) {
                        alert("결제 및 검증 성공");
                        window.location.href = '/';
                    } else {
                        // 검증 실패 시 결제 취소 로직 추가
                        await cancelPayment(rsp.imp_uid, myAmount);
                        alert("결제 검증 실패로 인해 결제가 취소되었습니다.");
                    }
                } catch (error) {
                    // 검증 요청 자체가 실패한 경우 결제 취소
                    await cancelPayment(rsp.imp_uid, myAmount);
                    console.log("Sending amount to server:", myAmount);
                    console.log("Used points:", usedPoints);
                    console.log("Product name:", productName);
                    alert("검증 실패로 인해 결제가 취소되었습니다: " + error.response.data);
                }
            } else {
                alert("결제 실패: " + rsp.error_msg);
            }
        }
    );
}

async function cancelPayment(imPortId, amount) {
    try {
        await axios.post('/payment/cancel', {
            imPortId: imPortId,
            amount: amount
        });
        console.log("결제 취소 성공");
    } catch (error) {
        console.error("결제 취소 실패:", error);
    }
}







document.addEventListener('DOMContentLoaded', function() {
    var modal = document.getElementById('addressModal');
    var btn = document.getElementById('changeAddressBtn');
    var span = document.getElementsByClassName('close')[0];
    var addressListDiv = document.getElementById('addressList');

    if (btn) {
        btn.onclick = function() {
            modal.style.display = 'block';
            // 주소 목록 렌더링
            addressListDiv.innerHTML = '';
            if (Array.isArray(addressList) && addressList.length > 0) {
                addressList.forEach(function(address, index) {
                    var addressDiv = document.createElement('div');
                    addressDiv.className = 'address-item';
                    addressDiv.innerHTML = `
                        <p><strong>${address.recipientName}</strong> (${address.recipientPhone})</p>
                        <p>${address.address} ${address.detailAddress}</p>
                        <p>요청사항: ${address.request}</p>
                        <button class="select-address-btn" data-index="${index}">선택</button>
                    `;
                    addressListDiv.appendChild(addressDiv);
                });
            } else {
                addressListDiv.innerHTML = '<p>등록된 주소가 없습니다.</p>';
            }
        }
    }

    span.onclick = function() {
        modal.style.display = 'none';
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    }

    // 주소 선택 이벤트
    addressListDiv.addEventListener('click', function(e) {
        if (e.target.classList.contains('select-address-btn')) {
            var index = e.target.getAttribute('data-index');
            var selectedAddress = addressList[index];

            // 선택된 주소로 폼 업데이트
            document.getElementById('recipientName').value = selectedAddress.recipientName;
            document.getElementById('recipientPhone').value = selectedAddress.recipientPhone;
            document.getElementById('address').value = selectedAddress.address + ' ' + selectedAddress.detailAddress;
            document.getElementById('delivery_request').value = selectedAddress.request;

            modal.style.display = 'none';
        }
    });
});