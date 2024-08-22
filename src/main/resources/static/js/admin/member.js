$(document).ready(function () {
// 정규식 변수
    let reg = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-_])(?=.*[0-9]).{8,15}$/;

    function convertEmptyToNull(value) {
        return value.trim() === "" ? null : value;
    }
    // 날짜 검색 현재 날짜 set
    function setEndDate() {
        let today = new Date();
        let year = today.getFullYear();
        let month = String(today.getMonth() + 1).padStart(2, '0');
        let day = String(today.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    // 데이터 불러오기
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

    // 검색 버튼 작동
    $("#btn-search").on("click", function () {
        loadList(0);
    });

    // 페이징
    $(document).on("click", ".pagination-controls button", function () {
        let page = $(this).data("page");
        loadList(page);
    });

    $("#endDate").val(setEndDate());

    loadList(0);

    // modal에 데이터 삽입
    function getModal(memberNo) {
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
    }

    // 회원 정보 모달 버튼 클릭
    $(document).on("click", ".btn-modal", function () {
        let memberNo = $(this).data("memberno");
        console.log(memberNo);
        
        getModal(memberNo);
    });
    
    // 닉네임 변경
    $(document).on("click", "#btn-change-nickname", function () {
        let memberNo = $(this).data("memberno");
        let nickName = $('#nickName').val().trim();
        let regId = /[^a-zA-Z0-9가-힣ㄱ-ㅎ]/g;
        console.log(memberNo);
            if (nickName != "" && nickName != "정보 없음") {
                if (regId.test(nickName)) {
                    swal("특수문자를 사용할 수 없습니다", "", "info");
                } else {
                    $.ajax({
                        url: "/admin/member/member-update",
                        type: "POST",
                        contentType: 'application/json',
                        data: JSON.stringify ({
                             memberNo: memberNo,
                             nickName: nickName 
                            }),
                        success: function (response) {
                            switch(response) {
                                case 1:
                                    swal("닉네임 변경 성공", "", "success");
                                    loadList(0);
                                    getModal(memberNo);
                                    break;
                                case 2:
                                    swal("닉네임 길이가 너무 깁니다", "", "info");
                                    break;
                                case 3:
                                    swal("중복된 닉네임입니다", "", "info");
                                    break;
                                case 0:
                                    swal("닉네임 변경 실패", "", "error");
                                    break;
                            }
                        },
                        error: function() { console.log("닉네임 변경 에러"); }
                    })
                }
         } else {
            swal("닉네임을 입력해 주세요.", "", "info");
         }
    });

    // 이메일 변경
    $(document).on("click", "#btn-change-email", function () {
        let memberNo = $(this).data("memberno");
        let email = $('#email').val().trim();

        let emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        let isValid = emailReg.test(email);

        console.log(memberNo);

        if (email != "" && email != "정보 없음") {
            if(!isValid) {
                swal("유효하지 않은 이메일입니다", "", "warning");
                return;
            }

            $.ajax({
                url: "/admin/member/member-update",
                type: "POST",
                contentType: 'application/json',
                data: JSON.stringify ({
                     memberNo: memberNo,
                     email: email 
                    }),
                success: function (response) {
                    switch(response) {
                        case 1:
                            swal("이메일 변경 성공", "", "success");
                            loadList(0);
                            getModal(memberNo);
                            break;
                        case 2:
                            swal("이메일 길이가 너무 깁니다", "", "warning");
                            break;
                        case 3:
                            swal("중복된 이메일입니다", "", "warning");
                        case 0:
                            swal("이메일 변경 실패", "", "error");
                            break;
                    }
                },
                error: function() { console.log("이메일 변경 에러"); }
            })
         } else {
            swal("이메일을 입력해 주세요", "", "warning");
         }
    });

    // 연락처 변경
    $(document).on("click", "#btn-change-tel", function () {
        let memberNo = $(this).data("memberno");
        let tel = $('#phoneNumInput').val().replace(/-/g, '').trim();
        let phoneReg = /^01[016789]\d{3,4}\d{4}$/;
        let isValid = phoneReg.test(tel);
        console.log(tel);

        if (tel != "" && tel != "정보 없음") {
            if(!isValid) {
                swal("유효하지 않은 연락처입니다", "", "warning");
                return;
            }

            $.ajax({
                url: "/admin/member/member-update",
                type: "POST",
                contentType: 'application/json',
                data: JSON.stringify ({
                     memberNo: memberNo,
                     tel: tel 
                    }),
                success: function (response) {
                    switch(response) {
                        case 1:
                            swal("연락처 변경 성공", "", "success");
                            loadList(0);
                            getModal(memberNo);
                            break;
                        case 2:
                            swal("연락처 길이가 너무 깁니다", "", "warning");
                            break;
                        case 0:
                            swal("연락처 변경 실패", "", "error");
                            break;
                    }
                },
                error: function() { console.log("연락처 변경 에러"); }
            })
         } else {
            swal("전화번호를 입력해 주세요", "", "info");
         }
    });

    // 패스워드 변경
    $(document).on("click", "#btn-change-password", function () {
        let memberNo = $(this).data("memberno");
        let password = $('#password').val().trim();
        console.log(memberNo);

        if (reg.test(password)) {
            if (password != "") { 
                $.ajax({
                    url: "/admin/member/member-update",
                    type: "POST",
                    contentType: 'application/json',
                    data: JSON.stringify ({
                         memberNo: memberNo,
                         password: password 
                        }),
                    success: function (response) {
                        switch(response) {
                            case 1:
                                swal("비밀번호 변경 성공", "", "success");
                                loadList(0);
                                getModal(memberNo);
                                break;
                            case 2:
                                swal("비밀번호 길이가 너무 깁니다", "", "warning");
                                break;
                            case 0:
                                swal("비밀번호 변경 실패", "", "error");
                                break;
                        }
                    },
                    error: function() { console.log("사용자 정보 로드 실패"); }
                })
             } else {
                swal("비밀번호를 입력해 주세요", "", "warning");
             }
        } else {
            swal("비밀번호는 8-15자 사이여야 하며, 알파벳, 숫자, 특수문자를 포함해야 합니다", "", "info");
        }
        
    });

    $(document).on("click", "#btn-delete", function () {
            let memberNo = $(this).data("memberno");
            console.log(memberNo);

            $.ajax({
                url: "/admin/member/member-delete",
                type: "POST",
                data: { memberNo: memberNo },
                success: function (response) {
                    switch(response) {
                        case 1:
                            swal("일반 회원 탈퇴 성공", "", "success");
                            loadList(0);
                            $('#memberModal').modal('hide');
                            break;
                        case 2:
                            swal("소셜 회원 탈퇴 성공.", "", "success");
                            loadList(0);
                            $('#memberModal').modal('hide');
                            break;
                        case 3:
                            swal("판매자 회원 탈퇴 성공", "", "success");
                            loadList(0);
                            $('#memberModal').modal('hide');
                            break;
                        default:
                            swal("회원 탈퇴 실패", "", "error");
                    }
                },
                error: function() { console.log("회원 탈퇴 에러"); }
            })
        });

});