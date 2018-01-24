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

4. Define a naming convention to separate responsibilities: Use actions (  ) for presenter and use UserEvent for view
View: OnqueryChanged(), OnSubmit(), onScrolledToEnd(), ShowProgress(), HideProgress(), NavigateToHome()
Presenter: loadData(), TryLogin()

5.Do not create Activity-lifecycle-style callbacks in the Presenter interface

7. Do not save the state inside the presenter as bundle is couple with andorid libs. Do it in your view.

8. Model should provides an innner interface for OnResultCallback listner - Through this presenter will contact the model.

9. Provide a cache for the Model to restore the View state - suggests caching network results using an interface like a Repository or anything with the aim to manage data, scoped to the application and not to the Activity.

10. Retain your data model, presenter is not for persistaing. 










[1] https://medium.com/@cervonefrancesco/model-view-presenter-android-guidelines-94970b430ddf
