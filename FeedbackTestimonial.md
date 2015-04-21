## Introduction ##

The following testimonials and mini-case studies are a testament to the success of the iBATIS framework.  You can expect these kinds of successes to continue in the future with MyBatis.

## Testimonials ##

I didn't want to talk about it until it was a 'core' piece of our
architecture, but MySpace.com is now running IBatis for a good portion of
its data access abstraction layer.

We like the flexibility. I came in from a 'heavier' ORM background but very
much appreciated the configurability IBatis offers against a changing data
storage medium, because we have a whole range of heavily denormalized
databases.

<...>

All-in-all I really like the system. I thought it was pretty cool that the
SqlMapper interface was so nicely abstracted that you could plug in generic
collections from 2.0 without a rewrite:
```
List<MyObject> l = new List<MyObject>();
Mapper.QueryForList("GetMyObjects",myParam,l);
```
<...>

Chris Bissell
Managing Architect
MySpace.com


---


"We tried them all. First OJB, then Hibernate, and we wound up finding out the best option last - IBATIS.

A year ago, we were naive Pojo-persistence beginners, and fell into usual the Hibernate trap pretty quickly. Complex code configuration with Middlegen, a gratuitously complicated data model, and the bloat and complexity that personifies Hibernate led us into a year-long period of darkness.

But Spring came early, in the form of a completely redone, simplified data model based on Spring DAO using Spring's Ibatis support (Spring class SqlMapClientDaoSupport).

The results are - quite frankly - astonishing. So simple, yet so powerful and easy-to-modify. Ibatis works perfectly, even for BLOBs, CLOBs, and the rest. IBATIS has dealt with our demanding application effectively and quickly with only a small amount of development effort invested thus far. The resulting Ibatis maps are simple and clear, and easy to understand and modify. And our performance is over 30% better than what we were seeing with sleepy Hibernate, all other things being equal.

So we would like to thank the Ibatis team, from the bottom of our hearts, for producing such a useful, clean, performant, and well-thought-out persistence mechanism for Java. Quite simply, IBATIS is the best.

Hats off to the IBATIS team!!"

craig vanderborgh
voxware incorporated


---


"Here is a gratuitous ego booster for Clinton, but I must say that I love the extra struts package in in the !JPetStore that allows mapping to bean methods. The use of thread local context is so nice not having to pass the servlet objects around.

I read about a lot of people complaining about struts, but I think that if they added this to their system then they wouldn't complain so much. Its a wonder that, AFAIK, this hasn't become either the standard or at least an option in the struts jar. Oh, and DispatchAction does not count.

(I know... [!OT], now I'll pretend to tie this in to iBatis) I used to use lots of Lists and Maps but now with iBatis and that new struts package, now my code is looking much more object oriented with much less boiler plate code."

Mark Bennet


---


"Hey Gang, just wanted to share some success that I have had using a struts/ibatis/mysql combo. I was tasked by my employer to create a client portal that allowed them to control aspects of their "stuff". I had already decided to do it in Struts/JSP using the good old MVC methodology. Originally I was going to use hibernate since I had used it before with some success. But in this case the database was "Legacy" and changing it required many levels of approval. Ibatis is a much easier tool to use when you have an existing db, I've found. Anyway I just had a demo at a trade show and it went very well. Here is a letter from the VP of sales regarding the show:

"First of all the show in NY went great!!!!

I wanted to take a minute to thank all of you for your hard work on the client portal. I just got back from the show and the feedback was fantastic!!!! Everyone that we presented the portal to was extremely impressed and said it will be a big benefit to them. The clients said that they do not know of anyone else that has this kind of tool and are really looking forward to using it on their future projects.

I know how hard everyone worked on this and I just wanted each of you to know how much I appreciate the effort."

I just wanted to share these small victories with all the developers that working hard on making OSS tools world class products."

Vincent


---


"Thanks ... You guys have done a great job with iBATIS and it has started getting a lot of traction because developers have started figuring out ORM is more hype (la EJB) than practical."

Antony Joseph


---


Here is a record of my experiences with Hibernate on a recent web project where I was the team lead and had complete control over choice of technology. This message is in the spirit of theory vs. practice.

A wise programmer once told me that "Simple things should be simple and hard things should be doable". I was recently on a new project where I was the team lead and I made the choice to use Hibernate. This was my first serious hibernate project and I found out that in Hibernate simple things are not so simple but the hard things are theoretically made easy by hibernate. However, I needed the simple things 80% of the time and the hard stuff about 20% of the time.

In my particular situation I had a very well designed data model that was built by a highly experienced data modeler. The data model was built a couple of weeks before I joined this project as the team lead and it had about 90 tables.

The task of mapping this data model to hibernate was not very easy, in the end we went with a one table one class mapping. We had many association tables that carried the reasons why the association was there in the first place. So we had to expose the association tables as mapped hibernate objects (This was also the recommendation of the Hibernate in Action book).

Developer productivity was the number one priority on this project, because we had a very tight deadline which we met). The idea behind ORM is that the developers would have only had to deal with the Java Objects but alas that was not doable they all ended up with print outs of the data model on their desks.

Also many of our web pages had data on them that was very easy to grab via SQL however it would have required a lot of code to traverse from one java object to another to present those pages. Following the advice of the Hibernate in Action book we ended up using report queries to get our data out of the database. Almost every screen that we did had one or two report queries.

The interesting thing is that the report queries in Hibernate turned out to be more complex to write and figure out. We frequently had to write queries in SQL then convert them to HQL. The whole idea of hibernate taking care of generating the SQL for you did not pan out. We still had to write a lot of queries.

Many of the tables we had had a lot of relationships to many other tables and I frequently found out that I could not map those relationships very easily to hibernate. At first I was tempted to go to the data modeler and ask him to change the relationships to make the hibernate mapping easier. The more I understood the business problem the more I figured out that he had done a great job and it was pointless to change the data model to try and make it more hibernate friendly. For the record the data modeler was great to work with and he did not object to any changes that we requested.

So I made the decision not to map the complex relationships to hibernate. Only simple relationships got mapped to hibernate, and this is another disappointment for me with ORM & Hibernate. It just seemed that I was always trying to fit a square into a circle.

In the end I had one java class for each table in the database, only simple relationships were mapped. Complex relationships were not mapped and that simplified our project quite a bit and allowed us to get it in on time. So we ended up with a lot of report queries in HQL which made use of the new operator in the HQL to stuff the results into java objects.

At the time I started this project I did not know much about iBATIS and I knew a lot more about Hibernate. Developer productivity was one of my key goals and I figured that it would be easier for me and my team to have access to books on hibernate and there were more learning resources for hibernate. If I was doing this project again I would have probably gone with iBATIS since I ended up using hibernate the iBATIS way!

My intuition as a programmer is that mapping statements to classes is better than mapping classes to tables, simply because it more naturally fits the principle that "simple things should be simple to do and hard things should be doable."

One of the key features of SQL is its closure property every SQL query returns a table. If some one was making an Object Query language they would not be able to keep the closure property or do projection in way that makes sense. This is why I think that mapping objects to statements is a better approach than mapping objects to tables.

I hope the write up of my experience was worth the time you spent reading it.

Adib Saikali


---


A pretty cool things in iBatis is that in QueryForObject it raises an exception when there are several results. It protected my code from mistakes

Xavier Combelle


---


We're been using iBatis for 2 years, and more we use it, more we love it. Simple, light, fast and easy to learn, since I met it for the first time I've never thought switching to other frameworks.

We adopted it in several projects and every time we deploy our applications, customers are always impressed by the performances and software design - mapping Java Objects to SQL Statements, instead of tables, has been being our "secret weapon"!!!

We are satisfied by this framework and we don't want to change it - the only feature we would like to have in next releases, is the possibility to plug, by programmatic way, external Data Sources and Caches engines, such Memcached.

Simone Tripodi
Head of Applications Development
asemantics.com