// 전역 변수 선언
let userName, originalPrice, possessionPoint;

// 숫자 포맷팅 함수
function formatNumber(num) {
    return num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,');
}

// DOM이 로드된 후 실행될 함수
document.addEventListener('DOMContentLoaded', function() {
    // Thymeleaf에서 전달된 데이터를 JavaScript 변수에 할당
    userName = document.getElementById('name').value;
    originalPrice = parseInt(document.getElementById('originalPrice').textContent.replace(/[^\d]/g, ''));
    possessionPoint = parseInt(document.getElementById('possessionPoint').textContent.replace(/[^\d]/g, ''));

    // 초기 총 결제금액 설정
    updateTotalPrice();

    // 이벤트 리스너 추가
    document.getElementById('pointInput').addEventListener('input', updateTotalPrice);
});

function updateTotalPrice() {
    const pointInput = document.getElementById('pointInput');
    const pointUsageDisplay = document.getElementById('pointUsageDisplay');
    const amountDisplay = document.getElementById('amount');

    let pointValue = parseInt(pointInput.value.replace(/,/g, '')) || 0;

    // 입력된 포인트가 보유 포인트를 초과하지 않도록 제한
    if (pointValue > possessionPoint) {
        pointValue = possessionPoint;
        pointInput.value = formatNumber(pointValue);
    }

    // 입력된 포인트가 총 판매가를 초과하지 않도록 제한
    if (pointValue > originalPrice) {
        pointValue = originalPrice;
        pointInput.value = formatNumber(pointValue);
    }

    let totalPrice = originalPrice - pointValue;

    // 총 결제금액이 0원 미만이 되지 않도록 제한
    if (totalPrice < 0) {
        totalPrice = 0;
        pointValue = originalPrice;
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

    // 장바구니의 모든 상품 정보를 가져옵니다.
    const products = Array.from(document.querySelectorAll('.product-info-table tbody tr')).map(row => ({
        productNo: Number(row.getAttribute('data-product-no')),
        productName: row.querySelector('td:nth-child(2)').textContent,
        quantity: Number(row.querySelector('td:nth-child(3) .quantity-value').textContent.trim().split(' ')[0]),
        price: Number(row.querySelector('td:nth-child(4) .price').textContent.replace(/[^\d]/g, '')),
        totalPrice: Number(row.querySelector('td:nth-child(4) .price').textContent.replace(/[^\d]/g, ''))
    }));

    IMP.request_pay(
        {
            pg: "html5_inicis",
            pay_method: "card",
            name: products.map(p => p.productName).join(', '),
            amount: myAmount,
            buyer_email: "gildong@gmail.com",
            buyer_name: userName,
            buyer_tel: "010-4242-4242",
            buyer_addr: "서울특별시 강남구 신사동",
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
                        products: products
                    });

                    if (data.success) {
                        alert("결제 및 검증 성공");
                        window.location.href = '/hello';
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