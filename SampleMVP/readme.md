Model-View-Presenter: Things to remember:
Each Android screen should have 3 components, Each comonent should have an interface and 

a) Model: it is an interface responsible for managing data. Model’s responsibilities include using APIs, caching data, managing databases and Network calls to update models
b) Presenter: The presenter is responsible for querying the model and updating the view when it got notification from model.
c) View: Just draw the UI( implemented by Activities, Fragments, any Android widget) and deletgates all the user evnets to Presenter.

Android Guidelines:

1. Make View dumb and passive, It just delatge all the userEvnets to presenter.

2. Make presenter framework-independent - Should not import any andoid libs. If you need android context to access resouce do it in view and if you need context to access shredPref do it in model.

3. Write a contract to describe the interaction between View and Presenter - You can have one comsoite interface or two indipendent interface. see [1]

4. Presenter has a 1-to-1 relation with the view. We might have a IBasePresenter {void attach(v)l void detach()}.

5. Define a naming convention to separate responsibilities: Use actions (  ) for presenter and use UserEvent for view
View: OnqueryChanged(), OnSubmit(), onScrolledToEnd(), ShowProgress(), HideProgress(), NavigateToHome()
Presenter: loadData(), TryLogin()

6.Do not create Activity-lifecycle-style callbacks in the Presenter interface

7. Do not save the state inside the presenter as bundle is couple with andorid libs. Do it in your view.

8. Model should provides an innner interface for OnResultCallback listner - Through this presenter will contact the model.

9. Provide a cache for the Model to restore the View state - suggests caching network results using an interface like a Repository or anything with the aim to manage data, scoped to the application and not to the Activity.

10. Retain your data model, presenter is not for persistaing, like store the network data, Store the page/scroll location etc.

11. Sometime, I will user Interactor instaed of model. These are classes which are supposed to interact with data by network API’s or from a Database or from local App Cache. The presenter will call getData() and it should then provide that data to Presenter.

12. Register and Unregister your presenter in onPause and onResume [2]. You need to create presenter object in activity so the instnace will be live in activity.

13. Where to initilized the Model object. I will prefer to do it in View and pass it presenter while contracting it. Presenter will have a refrebce to both view and model.

14. Dependecy Chain:
- View depens on Model(Interface/Class), Presenter( Interface and class)
- Presenter depends on View(Interface Only) and Model (Interface only)
- Model will not depends on any thing but acts on the listner it expose which is used by presenter.

14. Intereaction chain is like this:
- View only know presemnter - It it just deleugares all the userevnet to presenter. 
- Presenter knowns the View and mode, so it can invoke mode and view.
- Model doent know the view/Prsenter, It just send the data by calling the listner which is provided my presenter. So Model is totally indipendent.

15. Unit Testing:
- You can write the unit test for Model as Model will not depens on any things - It just a network call or save load using sqlite.
- You can write unit test on Presenter by moking model as It doent depends on View. As it is indipended to View( andrid spacific compoennt, it can be unit testied in non-android platform too.










[1] https://medium.com/@cervonefrancesco/model-view-presenter-android-guidelines-94970b430ddf
[2] https://android.jlelse.eu/android-mvp-doing-it-right-dac9d5d72079
