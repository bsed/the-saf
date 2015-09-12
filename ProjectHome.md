# SAF #
<h2>SAF(Simple Android Framework)是一个简单的android框架，它为开发Android app提供了基础性组件。</h2>
SAF已经在多个项目中使用，目前它刚刚到1.1版本，肯定会存在各种各样的问题。
这个项目第一次提交到google code是2012年的3月26号，我已经断断续续做了1年多了。稍后会把项目放到github上维护。遇到任何问题欢迎跟我的qq联系，qq：63067756

## SAF 功能 ##
  * [SAFApp](https://code.google.com/p/the-saf/#SAFApp)
  * [Event Bus](https://code.google.com/p/the-saf/#Event_Bus)
  * [Rest Client](https://code.google.com/p/the-saf/#Rest_Client)
  * [Image Cache](https://code.google.com/p/the-saf/#Image_Cache)
  * [Dependency Injection](https://code.google.com/p/the-saf/#Dependency_Injection)
  * [Sqlite ORM](https://code.google.com/p/the-saf/#Sqlite_ORM)
  * [Router](https://code.google.com/p/the-saf/#Router)
  * [Utils](https://code.google.com/p/the-saf/#Utils)

## SAFApp ##
SAFApp其实不能算是一个完整的模块，SAFApp继承了Application。增加了一个可作为缓存存放app全局变量的session，一个ImageLoader，一个记录Activity的List。

## Event Bus ##
事件总线框架，类似于google guava、square otto的event bus。它是一种消息发布-订阅模式,它的工作机制类似于观察者模式，通过通知者去注册观察者，最后由通知者向观察者发布消息。

Event Bus解耦了asyncTask、handler、thread、broadcast等组件。使用Event bus可以轻松地跨多个Fragment进行通讯。

它用法很简单，在Activity或者Fragment中使用，其中event是一个简单的POJO
```
       // 退出系统的事件
       eventBus.post(new LogoutEvent());
```

回调事件，同样在Activity或者Fragment中定义好。回调方法名可以随便定义，参数须要和event一一对应。并且在方法名前加上注解@Subscribe
```
	/**
	 * 退出整个app
	 * @param event
	 */
	@Subscribe
	public void onLogoutEvent(LogoutEvent event) {

	}
```

@Subscribe可以使用枚举
```
	/**
	 * 使用ThreadMode.BackgroundThread枚举，表示在后台线程运行，不在主线程中运行。
	 * @param event
	 */
	@Subscribe(ThreadMode.BackgroundThread)
	public void onBackendFresh(BackendFreshEvent event) {
	}
```

<a href='Hidden comment: 
This text will be removed from the rendered page.
'></a>
使用枚举BackgroundThread时，如果在回调方法中需要更新ui，则必须要配合handler使用。
在不使用枚举的情况下，@Subscribe会默认使用PostThread，表示回调方法会在主线程中运行。

如果在一个Activity中存在多个Fragment，并且在Activity或者在Fragment中存在订阅同一event的回调方法。如果发出event的请求时，这些回调方法都会起作用。

### Rest Client ###
Rest Client模块提供了http的get、post、put、delete方法。这个模块还不是很完善，只是适应自身项目需要，未来会不断增加新的功能。
这个模块没有基于apache httpclient，完全基于jdk中的HttpURLConnection。

同步调用get方法：
```
        RestClient client = RestClient.get(url);
        String body = client.body();
```
异步调用get方法：
```
        RestClient.get(url,new HttpResponseHandler(){

		public void onSuccess(String content) {
                      // content为http请求成功后返回的response
 		}
		
	});
```

同步调用post方法：post body内容为json
```
         RestClient client = RestClient.post(url);
         client.acceptJson().contentType("application/json", null);
         client.send(jsonString); // jsonString是已经由json对象转换成string类型
         String body = client.body();
```
异步调用post方法：post body内容为json
```
         RestClient.post(url,json,new HttpResponseHandler(){ // json对应的是fastjson的JSONObject对象

	        public void onSuccess(String content) {
	        }
			
         });
```
异步调用post方法：以form形式传递数据
```
	 RestClient.post(urlString, map, new HttpResponseHandler(){

		@Override
		public void onSuccess(String content) {

		}
					
	  });
```

## Image Cache ##
图片缓存模块包括2级缓存，内存中的cache和sd卡上存放在文件中的cache。<br>
图片缓存模块通过ImageLoader进行图片加载。<br>
如果app中使用了SAFApp，则无须创建新的ImageLoader就可以使用。<br>
<pre><code>          // 第一个参数是图片的url，第二个参数是ImageView对象，第三个参数是默认图片<br>
          imageLoader.displayImage(url, imageView ,R.drawable.defalut_icon);<br>
</code></pre>



<h2>Dependency Injection</h2>
Dependency Injection是依赖注入的意思，简称DI。<br>
SAF中的DI包括以下几个方面：<br>
<ul><li>Inject View ：简化组件的查找注册<br>
</li><li>Inject Service ：简化系统服务的注册，目前只支持android的系统服务<br>
</li><li>Inject Extra ：简化2个Activity之间Extra传递</li></ul>

<h3>Inject View</h3>
Inject View可以简化组件的查找注册，包括android自带的组件和自定义组件。<br>
在使用Inject View之前，我们会这样写代码<br>
<pre><code>public class MainActivity extends Activity {<br>
<br>
	private ImageView imageView;<br>
<br>
	@Override<br>
	protected void onCreate(Bundle savedInstanceState) {<br>
		super.onCreate(savedInstanceState);<br>
		<br>
		setContentView(R.layout.activity_main);<br>
		imageView = (ImageView) findViewById(R.id.imageview);<br>
        }<br>
}<br>
</code></pre>
在使用Inject View之后，会这样写代码<br>
<pre><code>public class MainActivity extends Activity {<br>
<br>
	@InjectView(id= R.id.imageview)<br>
	private ImageView imageView;<br>
	<br>
	@Override<br>
	protected void onCreate(Bundle savedInstanceState) {<br>
		super.onCreate(savedInstanceState);<br>
<br>
		setContentView(R.layout.activity_main);<br>
                Injector.injectInto(this);<br>
        }<br>
}<br>
</code></pre>
目前，@InjectView可用于Activity、Dialog、Fragment中。在Activity和Dialog用法相似，在Fragment中用法有一点区别。<br>
<pre><code>public class DemoFragment extends Fragment {<br>
<br>
	@InjectView(id=R.id.title)<br>
	private TextView titleView;<br>
	<br>
	@InjectView(id=R.id.imageview)<br>
	private ImageView imageView;<br>
	<br>
	@Override<br>
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {<br>
		View v = inflater.inflate(R.layout.fragment_demo, container, false);<br>
		<br>
		Injector.injectInto(this,v); // 和Activity使用的区别之处在这里<br>
		<br>
		initViews();<br>
		initData();<br>
		<br>
		return v;<br>
	}<br>
<br>
       ......<br>
}<br>
</code></pre>

<h3>Inject Extra</h3>
<pre><code>/**<br>
 * MainActivity传递数据给SecondActivity<br>
 * Intent i = new Intent(MainActivity.this,SecondActivity.class);						<br>
 * i.putExtra("test", "saf");<br>
 * i.putExtra("test_object", hello);<br>
 * startActivity(i);<br>
 * 在SecondActivity可以使用@InjectExtra注解<br>
 *<br>
 * @author Tony Shen<br>
 *<br>
 */<br>
public class SecondActivity extends Activity{<br>
<br>
	@InjectExtra(key="test")<br>
	private String testStr;<br>
	<br>
	@InjectExtra(key="test_object")<br>
	private Hello hello;<br>
	<br>
	protected void onCreate(Bundle savedInstanceState) {<br>
		super.onCreate(savedInstanceState);<br>
		<br>
		Injector.injectInto(this);<br>
		Log.i("++++++++++++","testStr="+testStr);<br>
		Log.i("++++++++++++","hello="+SAFUtil.printObject(hello)); // 该方法用于打印对象<br>
	}<br>
}<br>
</code></pre>

<h2>Sqlite ORM</h2>
顾名思义就是sqlite的orm框架，采用oop的方式简化对sqlite的操作。<br>
首先需要在AndroidManifest.xml中配上一些参数<br>
<pre><code>        &lt;!-- 表示在com.example.testsaf.db这个package下的类都是db的domain，一个类对应db里的一张表--&gt;<br>
        &lt;meta-data<br>
            android:name="DOMAIN_PACKAGE"<br>
            android:value="com.example.testsaf.db" /&gt;<br>
        <br>
       &lt;!-- 表示db的名称--&gt;<br>
        &lt;meta-data<br>
            android:name="DB_NAME"<br>
            android:value="testsaf.db" /&gt;<br>
 <br>
        &lt;!-- 表示db的版本号--&gt;<br>
         &lt;meta-data<br>
            android:name="DB_VERSION"<br>
            android:value="1" /&gt;<br>
</code></pre>
使用orm框架需要初始化DBManager，需要在Applicaion中完成。SAF中的SAFApp，没有初始化DBManager，如果需要使用SAFApp可以重写一个Application继承SAFApp，并初始化DBManager。<br>
<pre><code>/**<br>
 * @author Tony Shen<br>
 *<br>
 */<br>
public class TestApp extends Application{<br>
<br>
	@Override<br>
	public void onCreate() {<br>
		super.onCreate();<br>
		DBManager.initialize(this);<br>
	}<br>
<br>
}<br>
</code></pre>

db的domain使用是也是基于注解<br>
<pre><code>/**<br>
 * <br>
 * 表示sqlite中autocomplete表的属性<br>
 * @author Tony Shen<br>
 * <br>
 */<br>
@Table(name="autocomplete")<br>
public class Autocomplete extends DBDomain{<br>
<br>
	@Column(name="key_words",length=20,notNull=true)<br>
	public String KEY_WORDS;<br>
	<br>
	@Column(name="key_type",length=20,notNull=true)<br>
	public String KEY_TYPE;<br>
	<br>
	@Column(name="key_reference",length=80)<br>
	public String KEY_REFERENCE;<br>
}<br>
</code></pre>
db的操作很简单<br>
<pre><code>		Autocomplete auto = new Autocomplete();<br>
		auto.KEY_TYPE = "1";<br>
		auto.KEY_WORDS = "testtest";<br>
		auto.save(); // 插入第一条记录<br>
<br>
		Autocomplete auto2 = new Autocomplete();<br>
		auto2.KEY_TYPE = "0";<br>
		auto2.KEY_WORDS = "haha";<br>
		auto2.save(); // 插入第二条记录<br>
<br>
		Autocomplete auto3 = new Autocomplete().get(1); // 获取Autocomplete的第一条记录<br>
		if (auto3!=null) {<br>
			Log.i("+++++++++++++++","auto3.KEY_WORDS="+auto3.KEY_WORDS);<br>
		} else {<br>
			Log.i("+++++++++++++++","auto3 is null!");<br>
		}<br>
</code></pre>
查询结果集<br>
<pre><code>List&lt;Autocomplete&gt; list = new Autocomplete().executeQuery("select * from autocomplete where KEY_WORDS = 'testtest'");<br>
Log.i("+++++++++++++++","list.size()="+list.size());  // 根据sql条件查询<br>
		<br>
List&lt;Autocomplete&gt; list2 = new Autocomplete().executeQuery("select * from autocomplete where KEY_WORDS = ? and Id = ?","testtest","1");<br>
Log.i("+++++++++++++++","list2.size()="+list2.size()); // 表示查询select * from autocomplete where KEY_WORDS = 'testtest' and Id = '1'<br>
</code></pre>


<h2>Router</h2>
类似于rails的router功能，Activity之间、Fragment之间可以轻易实现相互跳转，并传递参数。<br>
使用Activity跳转必须在Application中做好router的映射。<br>
我们会做这样的映射，表示从某个Activity跳转到另一个Activity需要传递user、password2个参数<br>
<pre><code>Router.getInstance().setContext(getApplicationContext()); // 这一步是必须的，用于初始化Router<br>
Router.getInstance().map("user/:user/password/:password", SecondActivity.class);<br>
</code></pre>

有时候，activity跳转还会有动画效果，那么我们可以这么做<br>
<pre><code>RouterOptions options = new RouterOptions();<br>
options.enterAnim = R.anim.slide_right_in;<br>
options.exitAnim = R.anim.slide_left_out;<br>
Router.getInstance().map("user/:user/password/:password", SecondActivity.class, options);<br>
</code></pre>

在Application中定义好映射，activity之间跳转只需在activity中写下如下的代码，即可跳转到相应的Activity，并传递参数<br>
<pre><code>Router.getInstance().open("user/fengzhizi715/password/715");<br>
</code></pre>

如果在跳转前需要先做判断，看看是否满足跳转的条件,doCheck()返回false表示不跳转，true表示进行跳转到下一个activity<br>
<pre><code>Router.getInstance().open("user/fengzhizi715/password/715",new RouterChecker(){<br>
<br>
	public boolean doCheck() {<br>
	 	return true;<br>
	}<br>
 });<br>
</code></pre>

单独跳转到某个网页，调用系统电话，调用手机上的地图app打开地图等无须在Application中定义跳转映射。<br>
<pre><code>Router.getInstance().openURI("http://www.g.cn");<br>
<br>
Router.getInstance().openURI("tel://18662430000");<br>
<br>
Router.getInstance().openURI("geo:0,0?q=31,121");<br>
</code></pre>


Fragment之间的跳转也无须在Application中定义跳转映射。直接在某个Fragment写下如下的代码<br>
<pre><code>Router.getInstance().openFragment(new FragmentOptions(getFragmentManager(),new Fragment2()), R.id.content_frame);<br>
</code></pre>

当然在Fragment之间跳转可以传递参数<br>
<pre><code>Router.getInstance().openFragment("user/fengzhizi715/password/715",new FragmentOptions(getFragmentManager(),new Fragment2()), R.id.content_frame);<br>
</code></pre>
<h2>Utils</h2>
包含了很多常用的工具类，比如日期操作、字符串操作、SAFUtil里包含各种乱七八糟的常用类等等。<br>
<br>
<h2>反馈</h2>
如果您有任何反馈欢迎跟我的qq联系，qq：63067756