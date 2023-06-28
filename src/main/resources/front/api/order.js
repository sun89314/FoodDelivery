//提交订单
function  addOrderApi(data){
    return $axios({
        'url': '/order/submit',
        'method': 'post',
        data
      })
}

//查询所有订单
function orderListApi() {
  return $axios({
    'url': '/order/list',
    'method': 'get',
  })
}

//分页查询订单
function getUserInfo() {
    return $axios({
        'url': '/user/getUser',
        'method': 'get'
    })
}
function orderPagingApi(data) {
  return $axios({
      'url': '/order/userPage',
      'method': 'get',
      params:{...data}
  })
}

//再来一单
function orderAgainApi(data) {
  return $axios({
      'url': '/order/again',
      'method': 'post',
      data
  })
}