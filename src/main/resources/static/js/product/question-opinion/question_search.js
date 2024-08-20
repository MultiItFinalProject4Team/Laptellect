$(document).ready(function() {
    $('#questionSearch').on('submit', function(event) {
        event.preventDefault(); // 폼의 기본 제출 동작을 방지
        loadSearchComments(1);
    });
});

function loadSearchComments(page){
    var productNo = $('#questionSearch').find('input[name="productNo"]').val();
    var key = $('#searchSelect').val();
    var keyword = $('#question_search').val();
    var type = $('#typeField').val();
    var memberNo = $('#curmemberNo').val();
    console.log(productNo + key + keyword + type);

    $.ajax({
        url: "/customer/user/getQuestionSearch",
        type: "GET",
        data: {
            productNo: productNo,
            key: key,
            keyword: keyword,
            type: type,
            page: page
        },
        success:async function(response) {
            let commentUl = $("#customer-list");
            commentUl.empty(); // 선택 요소 비우기
            let totalPages = response.totalPages;
            let data = response.content;
            // 페이지네이션을 업데이트하는 함수 호출
            makeSearchPagination(page, totalPages);

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
        error: function() {
            swal("댓글을 로드하는 중 오류가 발생했습니다.", "", "error");
        }
    });
}
function makeSearchPagination(page, totalPages){
    let pagination = $('#pagination');
    pagination.empty();
    startPage = 1;

    if (page > 1) {
            pagination.append(`<li class="page-item"><a class="page-link prev_btn" onclick="loadSearchComments(${page - 1})">이전</a></li>`);
        } else {
            pagination.append(`<li class="page-item"><a class="page-link prev_btn disabled">이전</a></li>`);
        }

    for(let i=startPage; i<=totalPages; i++) { // 페이지네이션
        if (i === page) {
            pagination.append(`<li class="page-item active"><a class="page-link">${i}</a></li>`);
        } else {
            pagination.append(`<li class="page-item"><a class="page-link" onclick="loadSearchComments(${i}); return false;">${i}</a></li>`);
        }
    }

    if (page < totalPages) {
        pagination.append(`<li class="page-item"><a class="page-link next_btn" onclick="loadSearchComments(${page + 1})">다음</a></li>`);
    } else {
        pagination.append(`<li class="page-item"><a class="page-link next_btn disabled">다음</a></li>`);
    }
}

function formatDate(dateString) {
    var date = new Date(dateString);
    return `${date.getFullYear().toString().slice(-2)}/${('0' + (date.getMonth() + 1)).slice(-2)}/${('0' + date.getDate()).slice(-2)} ${('0' + date.getHours()).slice(-2)}:${('0' + date.getMinutes()).slice(-2)}`;
}