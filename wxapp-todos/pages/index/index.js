// 1. 完成页面结构、布局、样式
// 2. 设计数据结构
// 3. 完成数据绑定
// 4. 设计交互操作事件
// 5. 数据存储
var app = getApp()   //实例化小程序，从而获取全局数据或者使用全局函数
// console.log(app.globalData)
var MD5Util = require('../../utils/md5.js'); 

Page({
  // ===== 页面数据对象 =====
  data: {
    input: '',
    todos: [],
    leftCount: 0,
    allCompleted: false,
    logs: [],
    price: 0.01
  },
  save: function () {
    // 将 data 存储在本地缓存中指定的 key 中，会覆盖掉原来该 key 对应的内容，这是一个同步接口。
    wx.setStorageSync('todo_list', this.data.todos)
    wx.setStorageSync('todo_logs', this.data.logs)
  },
  load: function () {
    var todos = wx.getStorageSync('todo_list')
    if (todos) {
      var leftCount = todos.filter(function (item) {
        return !item.completed
      }).length
      this.setData({ todos: todos, leftCount: leftCount })
    }
    var logs = wx.getStorageSync('todo_logs')
    if (logs) {
      this.setData({ logs: logs })
    }
  },
  // ===== 页面生命周期方法 =====
  onLoad: function () {
    this.load();
  },
  // ===== 事件处理函数 =====
  inputChangeHandle: function (e) {
    this.setData({ input: e.detail.value })
  },
  addTodoHandle: function (e) {
    if (!this.data.input || !this.data.input.trim()) return
    var todos = this.data.todos
    todos.push({ name: this.data.input, completed: false })
    var logs = this.data.logs
    logs.push({ timestamp: new Date(), action: '新增', name: this.data.input })
    this.setData({
      input: '',
      todos: todos,
      leftCount: this.data.leftCount + 1,
      logs: logs
    })
    this.save()
  },
  toggleTodoHandle: function (e) {
    var index = e.currentTarget.dataset.index
    var todos = this.data.todos
    todos[index].completed = !todos[index].completed
    var logs = this.data.logs
    logs.push({
      timestamp: new Date(),
      action: todos[index].completed ? '标记完成' : '标记未完成',
      name: todos[index].name
    })
    this.setData({
      todos: todos,
      leftCount: this.data.leftCount + (todos[index].completed ? -1 : 1),
      logs: logs
    })
    this.save()
  },
  removeTodoHandle: function (e) {
    var index = e.currentTarget.dataset.index
    var todos = this.data.todos
    var remove = todos.splice(index, 1)[0]
    var logs = this.data.logs
    logs.push({ timestamp: new Date(), action: '移除', name: remove.name })
    this.setData({
      todos: todos,
      leftCount: this.data.leftCount - (remove.completed ? 0 : 1),
      logs: logs
    })
    this.save()
  },
  toggleAllHandle: function (e) {
    this.data.allCompleted = !this.data.allCompleted
    var todos = this.data.todos
    for (var i = todos.length - 1; i >= 0; i--) {
      todos[i].completed = this.data.allCompleted
    }
    var logs = this.data.logs
    logs.push({
      timestamp: new Date(),
      action: this.data.allCompleted ? '标记完成' : '标记未完成',
      name: '全部任务'
    })
    this.setData({
      todos: todos,
      leftCount: this.data.allCompleted ? 0 : todos.length,
      logs: logs
    })
    this.save()
  },
  clearCompletedHandle: function (e) {
    var todos = this.data.todos
    var remains = []
    for (var i = 0; i < todos.length; i++) {
      todos[i].completed || remains.push(todos[i])
    }
    var logs = this.data.logs
    logs.push({
      timestamp: new Date(),
      action: '清空',
      name: '已完成任务'
    })
    this.setData({ todos: remains, logs: logs })
    this.save()
  },
  wxPay: function (e) {
    var code = ''     //传给服务器以获得openId
    var timestamp = String(Date.parse(new Date()))   //时间戳
    var nonceStr =  ''   //随机字符串，后台返回
    var prepayId = ''    //预支付id，后台返回
    var paySign = ''     //加密字符串

    //获取用户登录状态
    wx.login({
        success: function(res) {
          if (res.code) { 
            code = res.code     
            //发起网络请求,发起的是HTTPS请求，向服务端请求预支付
            wx.request({
              url: 'https://i-test.com.cn/wxpay/prepay.do',
              data: {
                code: res.code
              },
              success: function(res) {
                if(res.data.result == true){
                  nonceStr = res.data.nonceStr
                  prepayId = res.data.prepayId
                  // 按照字段首字母排序组成新字符串
                  var payDataA = "appId="+app.globalData.appId+"&nonceStr="+res.data.nonceStr+"&package=prepay_id="+res.data.prepayId+"&signType=MD5&timeStamp="+timestamp;
                  var payDataB = payDataA + "&key=" + app.globalData.key; 
                  // 使用MD5加密算法计算加密字符串
                  paySign = MD5Util.MD5(payDataB).toUpperCase();
                  // 发起微信支付
                  wx.requestPayment({
                    'timeStamp': timestamp,
                    'nonceStr': nonceStr,
                    'package': 'prepay_id=' + prepayId,
                    'signType': 'MD5',
                    'paySign': paySign,
                    'success':function(res){
                    // 保留当前页面，跳转到应用内某个页面，使用wx.nevigeteBack可以返回原页面
                      wx.navigateTo({
                        url: '../pay/pay'
                      })
                    },
                    'fail':function(res){
                      console.log(res.errMsg)
                    }
                  })
                }else{
                  console.log('请求失败' + res.data.info)
                }
              }
            })
          } else {
            console.log('获取用户登录态失败！' + res.errMsg)
          }
        }
    });
  }
})
