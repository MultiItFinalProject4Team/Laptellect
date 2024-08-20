// 전역 변수 선언
let userName, productName, productPrice, originalPrice, possessionPoint;
let selectedAddressId = 1; // 선택된 주소의 ID를 저장할 변수

// 숫자 포맷팅 함수
function formatNumber(num) {
    return num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,');
}

// DOM이 로드된 후 실행될 함수
$(document).ready(function() {
    // Thymeleaf에서 전달된 데이터를 JavaScript 변수에 할당
    userName = $('#name').val();
    productName = $('.product-info-table tbody tr td:nth-child(2)').text();
    originalPrice = parseInt($('.price').text().replace(/[^\d]/g, ''));
    possessionPoint = parseInt($('.payment-price-row:nth-child(2) span:last-child').text().replace(/[^\d]/g, ''));

    // 초기 가격 설정
    $('#originalPrice').text(formatNumber(originalPrice) + '원');
    $('#amount').text(formatNumber(originalPrice) + '원');

    // 초기 총 결제금액 설정
    updateTotalPrice();

    // 이벤트 리스너 추가
    $('.quantity-minus').on('click', decreaseQuantity);
    $('.quantity-plus').on('click', increaseQuantity);
    $('#pointInput').on('keyup', updateTotalPrice);

    // 모달 관련 코드
    $('#changeAddressBtn').on('click', function() {
        $('#addressModal').show();
        renderAddressList();
    });

    $('#addAddressBtn').on('click', function() {
        $('#addressModal').hide();
        $('#addDeliveryModal').show();
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

//    // 배송지 추가 관련 코드
//    $(".addAddressBtn").prop("disabled", true);
//
//    $("#addDeliveryModal input").on("input", function() {
//        let allFieldsFilled = true;
//        $("#addDeliveryModal input").each(function() {
//            if ($(this).val().trim() === "") {
//                allFieldsFilled = false;
//                return false;
//            }
//        });
//        $(".addAddressBtn").prop("disabled", !allFieldsFilled);
//    });
//
//    $(".addAddressBtn").on("click", function () {
//        function convertEmptyToNull(value) {
//            return value.trim() === "" ? null : value;
//        }
//
//        let addressName = convertEmptyToNull($('#address-name').val());
//        let recipientName = convertEmptyToNull($('#recipient-name').val());
//        let postalCode = convertEmptyToNull($('#postal-code').val());
//        let address = convertEmptyToNull($('#address-input').val());
//        let detailAddress = convertEmptyToNull($('#detail-address').val());
//        let recipientPhone = convertEmptyToNull($('#recipient-phone').val());
//        let request = convertEmptyToNull($('#request').val());
//
//        console.log(addressName, recipientName, postalCode, address, detailAddress, recipientPhone, request);
//
//        $.ajax({
//            url: "/api/member/insert-address",
//            type: "POST",
//            contentType: "application/json",
//            data: JSON.stringify({
//                addressName: addressName,
//                recipientName: recipientName,
//                postalCode: postalCode,
//                address: address,
//                detailAddress: detailAddress,
//                recipientPhone: recipientPhone,
//                request: request
//            }),
//            success: function (response) {
//                switch(response) {
//                    case 1:
//                        alert("배송지 등록 성공");
//                        $('#addDeliveryModal').hide();
//
//                        // 새로 추가된 주소를 addressList에 추가
//                        addressList.push({
//                            addressName: addressName,
//                            recipientName: recipientName,
//                            recipientPhone: recipientPhone,
//                            address: address,
//                            detailAddress: detailAddress,
//                            request: request
//                        });
//
//                        $('#addressModal').show();
//                        renderAddressList();
//                        break;
//                    case 0:
//                        alert("배송지 이름을 입력해주세요.");
//                        break;
//                    case 101:
//                        alert("배송지는 최대 10개만 등록 가능합니다.");
//                        break;
//                    case 500:
//                        alert("배송지 등록 실패.");
//                        break;
//                }
//            },
//            error: function () {
//                alert("배송지 등록 실패");
//            }
//        });
//    });
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

function decreaseQuantity(event) {
    event.preventDefault();
    let quantity = parseInt($('#productQuantity').text());
    if (quantity > 1) {
        $('#productQuantity').text(quantity - 1);
        updatePriceDisplay();
        updateTotalPrice();
    }
}

function increaseQuantity(event) {
    event.preventDefault();
    let quantity = parseInt($('#productQuantity').text());
    $('#productQuantity').text(quantity + 1);
    updatePriceDisplay();
    updateTotalPrice();
}

function updatePriceDisplay() {
    let quantity = parseInt($('#productQuantity').text());
    let totalPrice = originalPrice * quantity;
    $('.price').text(formatNumber(totalPrice) + '원');
    // 판매가 업데이트
    $('#originalPrice').text(formatNumber(totalPrice) + '원');
}

function updateTotalPrice() {
    const pointInput = $('#pointInput');
    const pointUsageDisplay = $('#pointUsageDisplay');
    const amountDisplay = $('#amount');

    let quantity = parseInt($('#productQuantity').text());
    let pointValue = parseInt(pointInput.val().replace(/,/g, '')) || 0;

    // 입력된 포인트가 보유 포인트를 초과하지 않도록 제한
    if (pointValue > possessionPoint) {
        pointValue = possessionPoint;
        pointInput.val(formatNumber(pointValue));
    }

    let totalPrice = originalPrice * quantity - pointValue;

    // 총 결제금액이 100원 미만이 되지 않도록 제한
    if (totalPrice < 100) {
        swal('총 결제금액이 100원보다 작아질 수 없습니다', '', 'info');
        totalPrice = 100;
        pointValue = originalPrice * quantity - 100;
        pointInput.val(formatNumber(pointValue));
    }

    pointUsageDisplay.text(formatNumber(pointValue) + '원');
    amountDisplay.text(formatNumber(totalPrice) + '원');
}

function mypayment() {
    const myAmount = Number($('#amount').text().replace(/[^\d]/g, ''));
    const usedPoints = Number($('#pointInput').val().replace(/,/g, '')) || 0;
    const quantity = parseInt($('#productQuantity').text());
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
                        productName: productName,
                        addressId: selectedAddressId,
                        quantity: quantity
                    });

                    if (data.success) {
                        swal('결제 및 검증 성공', '', 'success');
                        window.location.href = data.redirectUrl;
                    } else {
                        // 검증 실패 시 결제 취소 로직 추가
                        await cancelPayment(rsp.imp_uid, myAmount);
                        swal('결제 검증 실패로 인해 결제가 취소되었습니다.', '', 'error');
                    }
                } catch (error) {
                    // 검증 요청 자체가 실패한 경우 결제 취소
                    await cancelPayment(rsp.imp_uid, myAmount);
                    console.log("Sending amount to server:", myAmount);
                    console.log("Used points:", usedPoints);
                    console.log("Product name:", productName);
                    console.log("Address ID:", selectedAddressId);
                    console.log("quantity:", quantity);
                    swal('검증 실패로 인해 결제가 취소되었습니다.', error.response.data, 'error');
                }
            } else {
                swal('결제 실패', rsp.error_msg, 'error');
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

//// 우편번호 찾기 함수
//function postCode() {
//    new daum.Postcode({
//        oncomplete: function(data) {
//            var addr = ''; // 주소 변수
//            var extraAddr = ''; // 참고항목 변수
//
//            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
//                addr = data.roadAddress;
//            } else { // 사용자가 지번 주소를 선택했을 경우(J)
//                addr = data.jibunAddress;
//            }
//
//            if(data.userSelectedType === 'R'){
//                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
//                    extraAddr += data.bname;
//                }
//                if(data.buildingName !== '' && data.apartment === 'Y'){
//                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
//                }
//                if(extraAddr !== ''){
//                    extraAddr = ' (' + extraAddr + ')';
//                }
//            }
//
//            // 우편번호와 주소 정보를 배송지 추가하기 모달의 필드에 넣는다.
//            document.getElementById('postal-code').value = data.zonecode;
//            document.getElementById("address-input").value = addr;
//
//            // 상세주소 필드로 커서 이동
//            document.getElementById("detail-address").focus();
//        }
//    }).open();
//}