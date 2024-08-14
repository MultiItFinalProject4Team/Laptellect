//각 js에 이 페이징을 적용 시켜주면 댈듯?
function loadComments(page) {
    var productNo = $('#curproductNo').val();
    var memberNo = $('#curmemberNo').val();
    $.ajax({
        type: "GET",
        url: `/customer/user/get_AllproductqList`, // URL에서 잘못된 닫는 괄호 제거
        data: {
            productNo: productNo,
            page: page // 페이지 번호를 올바르게 전달
        },
        dataType: 'json', // 데이터 타입 명시
        success:async function (response) {
            let commentUl = $("#customer-list");
            commentUl.empty(); // 선택 요소 비우기
            console.log("현재 페이지: "+page)
            let totalPages = response.totalPages;
            let data = response.content;
            // 페이지네이션을 업데이트하는 함수 호출
            makePagination(page, totalPages);


            // 댓글을 리스트에 추가
            for (const productqList of data) {
                    if (productqList.answer === 'Y') {
                        console.log("답변 존재: " + productqList.productqNo);

                        try {
                            let productaList = await $.ajax({
                                type: "GET",
                                url: `/customer/user/get_AllproductaList`,
                                data: {
                                    productqNo: productqList.productqNo
                                },
                                dataType: 'json'
                            });

                            console.log(productaList.content);
                            const commentItemHtml2 = createCommentItem2(productqList, productaList, memberNo);
                            $('#customer-list').append(commentItemHtml2);
                        } catch (error) {
                            console.error("Error fetching product list:", error);
                        }
                    } else if (productqList.answer === 'N') {
                        const commentItemHtml = createCommentItem(productqList, memberNo);
                        $('#customer-list').append(commentItemHtml);
                    }
            }
        },
        error: function () {
            alert("댓글을 로드하는 중 오류가 발생했습니다.");
        }
    });
}

function makePagination(page, totalPages){
    let pagination = $('#pagination');
    pagination.empty();
    startPage = 1;

    if (page > 1) {
            pagination.append(`<li class="page-item"><a class="page-link prev_btn" onclick="loadComments(${page - 1})">이전</a></li>`);
        } else {
            pagination.append(`<li class="page-item"><a class="page-link prev_btn disabled">이전</a></li>`);
        }

    for(let i=startPage; i<=totalPages; i++) { // 페이지네이션
        if (i === page) {
            pagination.append(`<li class="page-item active"><a class="page-link">${i}</a></li>`);
        } else {
            pagination.append(`<li class="page-item"><a class="page-link" onclick="loadComments(${i}); return false;">${i}</a></li>`);
        }
    }

    if (page < totalPages) {
        pagination.append(`<li class="page-item"><a class="page-link next_btn" onclick="loadComments(${page + 1})">다음</a></li>`);
    } else {
        pagination.append(`<li class="page-item"><a class="page-link next_btn disabled">다음</a></li>`);
    }
}

function formatDate(dateString) {
    var date = new Date(dateString);
    return `${date.getFullYear().toString().slice(-2)}/${('0' + (date.getMonth() + 1)).slice(-2)}/${('0' + date.getDate()).slice(-2)} ${('0' + date.getHours()).slice(-2)}:${('0' + date.getMinutes()).slice(-2)}`;
}

$(document).ready(function () {
    loadComments(1);
    }
);