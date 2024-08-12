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
        success: function(response) {
            let commentUl = $("#customer-list");
            commentUl.empty(); // 선택 요소 비우기
            let totalPages = response.totalPages;
            let data = response.content;
            // 페이지네이션을 업데이트하는 함수 호출
            makeSearchPagination(page, totalPages);

            // 댓글을 리스트에 추가
            data.forEach(function(productqList) {
                const categoryText = productqList.productqCategoryCode === 'productq_opinion' ? '의견' : '문의';
                const categoryClass = productqList.productqCategoryCode === 'productq_opinion' ? 'opinion-class' : 'question-class';
                var newListItem = `
                    <div class="question">
                        <div class="question-header">
                            <div class="question-author">
                                <span class="author-name">${productqList.memberName || '정보 없음'}</span>
                                <span class="question-date">${formatDate(productqList.createdAt)}</span>
                            </div>
                        </div>
                        <div class="question-body">
                            <div class="question-bodytop">
                                <p class="question-category ${categoryClass}">${categoryText}</p>
                                <h4 class="question-title">
                                    ${(productqList.secret === 'Y' && productqList.memberNo != memberNo) ? '비밀글입니다 🔒' : productqList.title}
                                </h4>
                            </div>
                            <div class="question-content">
                                <p class="question-content">
                                    ${(productqList.secret === 'Y' && productqList.memberNo != memberNo) ? '본인만 확인 가능합니다' : productqList.content}
                                </p>
                            </div>
                        </div>
                        <hr class="question-hr">
                    </div>
                `;
                $('#customer-list').append(newListItem);
            });
        },
        error: function() {
            alert("댓글을 로드하는 중 오류가 발생했습니다.");
        }
    });
}
function makeSearchPagination(page, totalPages){
    let pagination = $('#pagination');
    pagination.empty();
    startPage = 1;

    if (page > 1) {
            pagination.append(`<li><a class="prev_btn" onclick="loadSearchComments(${page - 1})">이전</a></li>`);
        } else {
            pagination.append(`<li><a class="prev_btn disabled">이전</a></li>`);
        }

    for(let i=startPage; i<=totalPages; i++) { // 페이지네이션
        if (i === page) {
            pagination.append(`<li class="active"><a>${i}</a></li>`);
        } else {
            pagination.append(`<li><a onclick="loadSearchComments(${i}); return false;">${i}</a></li>`);
        }
    }

    if (page < totalPages) {
        pagination.append(`<li><a class="next_btn" onclick="loadSearchComments(${page + 1})">다음</a></li>`);
    } else {
        pagination.append(`<li><a class="next_btn disabled">다음</a></li>`);
    }
}

function formatDate(dateString) {
    var date = new Date(dateString);
    return `${date.getFullYear().toString().slice(-2)}/${('0' + (date.getMonth() + 1)).slice(-2)}/${('0' + date.getDate()).slice(-2)} ${('0' + date.getHours()).slice(-2)}:${('0' + date.getMinutes()).slice(-2)}`;
}