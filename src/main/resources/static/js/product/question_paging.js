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
        success: function (response) {
            let commentUl = $("#customer-list");
            commentUl.empty(); // 선택 요소 비우기
            console.log("현재 페이지: "+page)
            let totalPages = response.totalPages;
            let data = response.content;
            // 페이지네이션을 업데이트하는 함수 호출
            makePagination(page, totalPages);

            // 댓글을 리스트에 추가
            data.forEach(function (productqList) {
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
            pagination.append(`<li><a class="prev_btn" onclick="loadComments(${page - 1})">이전</a></li>`);
        } else {
            pagination.append(`<li><a class="prev_btn disabled">이전</a></li>`);
        }

    for(let i=startPage; i<=totalPages; i++) { // 페이지네이션
        if (i === page) {
            pagination.append(`<li class="active"><a>${i}</a></li>`);
        } else {
            pagination.append(`<li><a onclick="loadComments(${i}); return false;">${i}</a></li>`);
        }
    }

    if (page < totalPages) {
        pagination.append(`<li><a class="next_btn" onclick="loadComments(${page + 1})">다음</a></li>`);
    } else {
        pagination.append(`<li><a class="next_btn disabled">다음</a></li>`);
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