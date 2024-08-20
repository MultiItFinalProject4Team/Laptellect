function createCommentItem(productqList, memberNo) {
    const categoryText = productqList.productqCategoryCode === 'productq_opinion' ? '의견' : '문의';
    const categoryClass = productqList.productqCategoryCode === 'productq_opinion' ? 'opinion-class' : 'question-class';
    const curauth = $('#curauth').val();
    return `
        <div class="question">
            <div class="question-header">
                <div class="question-author">
                    <span class="author-name">${productqList.memberName || '정보 없음'}</span>
                    <span class="question-date">${formatDate(productqList.createdAt)}</span>
                    <a class="question-update" onclick="updateQuestion(${productqList.productqNo})">${(productqList.memberNo == memberNo) ? '수정/':''}</a>
                    <a class="question-delete" onclick="confirmDelete(${productqList.productqNo})">${(productqList.memberNo == memberNo) ? '삭제':''}</a>
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
                <div class="question-reply">
                    <a class="question-reply" onclick="appReply(${productqList.productqNo})">
                        ${(curauth == 'ADMIN') ? '답글':''}
                    </a>
                </div>
            </div>
            <hr class="question-hr">
    </div>
    `;
}

function createCommentItem2(productqList, productaList, memberNo) {
    const categoryText = productqList.productqCategoryCode === 'productq_opinion' ? '의견' : '문의';
    const categoryClass = productqList.productqCategoryCode === 'productq_opinion' ? 'opinion-class' : 'question-class';
    const curauth = $('#curauth').val();
    const memberName = $('#curmemname').val();
    console.log(curauth);
    return `
        <div class="question">
            <div class="question-header">
                <div class="question-author">
                    <span class="author-name">${productqList.memberName || '정보 없음'}</span>
                    <span class="question-date">${formatDate(productqList.createdAt)}</span>
                    <a class="question-update" onclick="updateQuestion(${productqList.productqNo})">${(productqList.memberNo == memberNo && productqList.answer=='N') ? '수정/':''}</a>
                    <a class="question-delete" onclick="confirmDelete(${productqList.productqNo})">${(productqList.memberNo == memberNo || curauth == 'ADMIN') ? '삭제':''}</a>
                </div>
            </div>
            <div class="question-body">
                <div class="question-bodytop">
                    <p class="question-category ${categoryClass}">${categoryText}</p>
                    <h4 class="question-title">
                        ${((productqList.secret === 'Y' && productqList.memberNo != memberNo) && curauth != 'ADMIN') ? '비밀글입니다 🔒' : productqList.title}
                    </h4>
                </div>
                <div class="question-content">
                    <p class="question-content">
                        ${((productqList.secret === 'Y' && productqList.memberNo != memberNo)&& curauth != 'ADMIN') ? '본인만 확인 가능합니다' : productqList.content}
                    </p>
                </div>
            </div>
    <div class="reply">
        <div class="reply-header">
            <div class="reply-author">
                <h2>↪</h2>
                <span class="reply-name">${curauth=='관리자'? '관리자' : memberName}</span>
                <span class="reply-date">${formatDate(productaList.createdAt)}</span>
                <a class="reply-update" onclick="updateQuestionReply(${productqList.productqNo})">${(curauth == 'ADMIN') ? '수정/':''}</a>
                <a class="reply-delete" onclick="confirmDeleteReply(${productqList.productqNo})">${(curauth == 'ADMIN') ? '삭제':''}</a>
            </div>
        </div>
        <div class="reply-body">
            <div class="reply-bodytop">
                <h4 class="reply-title">
                    ${((productqList.secret === 'Y' && productqList.memberNo != memberNo) && curauth != 'ADMIN') ? '비밀글입니다 🔒' : productaList.title}
                </h4>
            </div>
            <div class="reply-content">
                <p class="reply-content">
                    ${((productqList.secret === 'Y' && productqList.memberNo != memberNo) && curauth != 'ADMIN') ? '본인만 확인 가능합니다' : productaList.content}
                </p>
            </div>
        </div>
        </div>
        <hr class="question-hr">
    </div>
    `;
}