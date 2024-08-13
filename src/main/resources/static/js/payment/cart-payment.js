// 전역 변수 선언
let userName, originalPrice, possessionPoint;
let selectedAddressId = 1; // 선택된 주소의 ID를 저장할 변수

// 숫자 포맷팅 함수
function formatNumber(num) {
    return num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,');
}

// DOM이 로드된 후 실행될 함수
$(document).ready(function() {
    // Thymeleaf에서 전달된 데이터를 JavaScript 변수에 할당
    userName = $('#name').val();
    originalPrice = parseInt($('#originalPrice').text().replace(/[^\d]/g, ''));
    possessionPoint = parseInt($('#possessionPoint').text().replace(/[^\d]/g, ''));

    // 초기 총 결제금액 설정
    updateTotalPrice();

    // 이벤트 리스너 추가
    $('#pointInput').on('keyup', updateTotalPrice);

    // 모달 관련 코드
    $('#changeAddressBtn').on('click', function() {
        $('#addressModal').show();
        renderAddressList();
    });

    $('.close').on('click', function() {
        $(this).closest('.modal').hide();
    });

    $(window).on('click', function(event) {
        if ($(event.target).hasClass('modal')) {
            $('.modal').hide();
        }
    });

    // 주소 선택 이벤트
    $('#addressList').on('click', '.select-address-btn', function() {
        var index = $(this).data('index');
        var selectedAddress = addressList[index];

        // 선택된 주소로 폼 업데이트
        updateDeliveryInfo(selectedAddress);

        // 선택된 주소의 ID 저장
        selectedAddressId = selectedAddress.addressId;

        $('#addressModal').hide();
    });
});

function updateDeliveryInfo(address) {
    $('#recipientName').val(address.recipientName);
    $('#recipientPhone').val(address.recipientPhone);
    $('#address').val(address.address + ' ' + address.detailAddress);
    $('#delivery_request').val(address.request);
}

function renderAddressList() {
    var addressListDiv = $('#addressList');
    if (addressListDiv.length) {
        addressListDiv.empty();
        if (Array.isArray(addressList) && addressList.length > 0) {
            addressList.forEach(function(address, index) {
                var addressDiv = $('<div>').addClass('address-item');
                addressDiv.html(`
                    <div class="address-content">
                        <p><strong>${address.addressName}</strong></p>
                        <p><strong>${address.recipientName}</strong> (${address.recipientPhone})</p>
                        <p>${address.address} ${address.detailAddress}</p>
                        <p>요청사항: ${address.request}</p>
                    </div>
                    <button class="select-address-btn" data-index="${index}">선택</button>
                `);
                addressListDiv.append(addressDiv);
            });
        } else {
            addressListDiv.html('<p>등록된 주소가 없습니다.</p>');
        }
    }
}

function updateTotalPrice() {
    const pointInput = $('#pointInput');
    const pointUsageDisplay = $('#pointUsageDisplay');
    const amountDisplay = $('#amount');

    let pointValue = parseInt(pointInput.val().replace(/,/g, '')) || 0;

    // 입력된 포인트가 보유 포인트를 초과하지 않도록 제한
    if (pointValue > possessionPoint) {
        pointValue = possessionPoint;
        pointInput.val(formatNumber(pointValue));
    }

    // 입력된 포인트가 총 판매가를 초과하지 않도록 제한
    if (pointValue > originalPrice) {
        pointValue = originalPrice;
        pointInput.val(formatNumber(pointValue));
    }

    let totalPrice = originalPrice - pointValue;

    // 총 결제금액이 0원 미만이 되지 않도록 제한
    if (totalPrice < 0) {
        totalPrice = 0;
        pointValue = originalPrice;
        pointInput.val(formatNumber(pointValue));
    }

    pointUsageDisplay.text(formatNumber(pointValue) + '원');
    amountDisplay.text(formatNumber(totalPrice) + '원');
}

function mypayment() {
    const myAmount = Number($('#amount').text().replace(/[^\d]/g, ''));
    const usedPoints = Number($('#pointInput').val().replace(/,/g, '')) || 0;
    const IMP = window.IMP;
    IMP.init("imp64527455");

    // 장바구니의 모든 상품 정보를 가져옵니다.
    const products = Array.from($('.product-info-table tbody tr')).map(row => ({
        productNo: Number($(row).attr('data-product-no')),
        productName: $(row).find('td:nth-child(2)').text(),
        quantity: Number($(row).find('td:nth-child(3) .quantity-value').text().trim().split(' ')[0]),
        price: Number($(row).find('td:nth-child(4) .price').text().replace(/[^\d]/g, '')),
        totalPrice: Number($(row).find('td:nth-child(4) .price').text().replace(/[^\d]/g, ''))
    }));

    IMP.request_pay(
        {
            pg: "html5_inicis",
            pay_method: "card",
            name: products.map(p => p.productName).join(', '),
            amount: myAmount,
            buyer_email: $('#email').val(),
            buyer_name: userName,
            buyer_tel: $('#tel').val(),
            buyer_addr: $('#address').val(),
            buyer_postcode: "01181",
            m_redirect_url: "",
        },
        async (rsp) => {
            if (rsp.success) {
                try {
                    const { data } = await axios.post('/payment/verifyCartPayment', {
                        imPortId: rsp.imp_uid,
                        totalAmount: myAmount,
                        usedPoints: usedPoints.toString(),
                        products: products,
                        addressId: selectedAddressId // 선택된 주소 ID 추가
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
                    console.log("Products:", products);
                    console.log("Address ID:", selectedAddressId);
                    alert("검증 실패로 인해 결제가 취소되었습니다: " + (error.response ? error.response.data.message : error.message));
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