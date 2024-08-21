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
            swal("댓글을 로드하는 중 오류가 발생했습니다.", "", "error");
        }
    });
}

function makePagination(page, totalPages) {
    let pagination = $('#pagination');
    pagination.empty();

    const pageSize = 10; // 한 번에 표시할 페이지 번호 개수
    const currentGroup = Math.floor((page - 1) / pageSize); // 현재 페이지 그룹 (0부터 시작)
    const startPage = currentGroup * pageSize + 1;
    const endPage = Math.min(startPage + pageSize - 1, totalPages);
    console.log(startPage);
    console.log(endPage);

    // 이전 10페이지 버튼
    if (startPage > 1) {
        pagination.append(`<li class="page-item"><a class="page-link prev_btn" onclick="loadComments(${startPage - 1})"><<</a></li>`);
    } else {
        pagination.append(`<li class="page-item"><a class="page-link prev_btn disabled"><<</a></li>`);
    }

    // 이전 페이지 버튼
    if (page > 1) {
        pagination.append(`<li class="page-item"><a class="page-link prev_btn" onclick="loadComments(${page - 1})">이전</a></li>`);
    } else {
        pagination.append(`<li class="page-item"><a class="page-link prev_btn disabled">이전</a></li>`);
    }

    // 페이지 번호
    for (let i = startPage; i <= endPage; i++) {
        if (i === page) {
            pagination.append(`<li class="page-item active"><a class="page-link">${i}</a></li>`);
        } else {
            pagination.append(`<li class="page-item"><a class="page-link" onclick="loadComments(${i}); return false;">${i}</a></li>`);
        }
    }

    // 다음 페이지 버튼
    if (page < totalPages) {
        pagination.append(`<li class="page-item"><a class="page-link next_btn" onclick="loadComments(${page + 1})">다음</a></li>`);
    } else {
        pagination.append(`<li class="page-item"><a class="page-link next_btn disabled">다음</a></li>`);
    }

    // 다음 10페이지 버튼
    if (endPage < totalPages) {
        pagination.append(`<li class="page-item"><a class="page-link next_btn" onclick="loadComments(${endPage + 1})">>></a></li>`);
    } else {
        pagination.append(`<li class="page-item"><a class="page-link next_btn disabled">>></a></li>`);
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