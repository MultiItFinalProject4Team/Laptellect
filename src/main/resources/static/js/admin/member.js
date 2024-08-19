$(document).ready(function () {
    function convertEmptyToNull(value) {
        return value.trim() === "" ? null : value;
    }

    function setEndDate() {
        let today = new Date();
        let year = today.getFullYear();
        let month = String(today.getMonth() + 1).padStart(2, '0');
        let day = String(today.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    function loadList(page) {
        let startDate = convertEmptyToNull($("#startDate").val());
        let endDate = convertEmptyToNull($("#endDate").val());
        let cate = convertEmptyToNull($(".form-select").val());
        let keyword = convertEmptyToNull($("#keyword").val());

        $.ajax({
            url: "/admin/member/list",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                page: page,
                size: 10,
                startDate: startDate,
                endDate: endDate,
                cate: cate,
                keyword: keyword,
            }),
            success: function (response) {
                $("#member-list-box").html(response);
            },
            error: function (xhr, status, error) {
                console.error("조회 실패:", error);
            },
        });
    }

    $("#btn-search").on("click", function () {
        loadList(0);
    });

    $(document).on("click", ".pagination-controls button", function () {
        let page = $(this).data("page");
        loadList(page);
    });

    $("#endDate").val(setEndDate());

    loadList(0);

    $(document).on("click", ".btn-modal", function () {
        let memberNo = $(this).data("memberno");
        console.log(memberNo);
        
        $.ajax({
            url: "/admin/member/member-info",
            type: "POST",
            data: { memberNo: memberNo },
            success: function (response) {
                console.log("사용자 정보 로드 완료");
                $("#myTabContent").html(response);
            },
            error: function() { console.log("사용자 정보 로드 실패"); }
        })
    });
});