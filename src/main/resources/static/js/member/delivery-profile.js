$(function () {
      $(document).ready(function () {

        function getAddressList() {
            $.ajax({
                url: "/api/member/deliveryList",
                type: 'GET',
                success: function(response) {
                    $('#address-box').html(response);
                },
                error: function() {
                    swal("오류", "서버 오류가 발생했습니다.", "error");
                }
            });
        }

        getAddressList();

        $(".error-message").hide();
//        $(".addAddressBtn").prop("disabled", true);

        $(".addAddressBtn").on("click", function () {
          function convertEmptyToNull(value) {
                  return value.trim() === "" ? null : value;
          }

          let addressName = convertEmptyToNull($('#address-name').val());
          let recipientName = convertEmptyToNull($('#recipient-name').val());
          let postalCode = convertEmptyToNull($('#postal-code').val());
          let address = convertEmptyToNull($('#address').val());
          let detailAddress = convertEmptyToNull($('#detail-address').val());
          let recipientPhone = convertEmptyToNull($('#recipient-phone').val());
          let request = convertEmptyToNull($('#request').val());

          if (!addressName) {
              swal("필수 입력 항목", "저장할 배송지 이름을 입력해주세요.", "info");
          } else if (!recipientName) {
              swal("필수 입력 항목", "수령인 이름을 입력해주세요.", "info");
          } else if (!postalCode) {
              swal("필수 입력 항목", "우편번호를 입력해주세요.", "info");
          } else if (!address) {
              swal("필수 입력 항목", "주소를 입력해주세요.", "info");
          } else if (!detailAddress) {
              swal("필수 입력 항목", "상세 주소를 입력해주세요.", "info");
          } else if (!recipientPhone) {
              swal("필수 입력 항목", "수령인 전화번호를 입력해주세요.", "info");
          } else if (!request) {
              swal("필수 입력 항목", "요청 사항을 입력해주세요.", "info");
          } else {
              console.log(addressName, addressName, recipientName, postalCode, address, detailAddress, recipientPhone, request);

              $.ajax({
                  url: "/api/member/insert-address",
                  type: "POST",
                  contentType: "application/json",
                  data: JSON.stringify({
                      addressName: addressName,
                      recipientName: recipientName,
                      postalCode: postalCode,
                      address: address,
                      detailAddress: detailAddress,
                      recipientPhone: recipientPhone,
                      request: request
                  }),
                  success: function (response) {
                      switch(response) {
                        case 1:
                            swal("성공", "배송지가 성공적으로 등록되었습니다.", "success");

                            setTimeout(function() {
                                $('#add-delivery-modal').modal('hide');
                                getAddressList();
                                location.reload();
                            }, 1000);

                            break;
                        case 0:
                            swal("경고", "배송지 이름을 입력해주세요.", "warning");
                            break;
                        case 101:
                            swal("경고", "배송지는 최대 10개만 등록 가능합니다.", "warning");
                            break;
                        case 500:
                            swal("오류", "배송지 등록에 실패했습니다.", "error");
                            break;
                      }
                  },
                  error: function () {
                      swal("오류", "배송지 등록에 실패했습니다.", "error");
                  }
              });
          }
        });

        // 배송지 삭제 이벤트 리스너 추가
        $(document).on('click', '.delete-address', function() {
            // 여기에 배송지 삭제 로직 추가
            // 삭제 완료 후 페이지 새로고침
            location.reload();
        });
      });
    });