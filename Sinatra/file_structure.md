This app will follow a file structure similar to rails

Sinatra/
..../app
......../controller
......../helpers
......../views
............../layouts
..../doc
..../log
..../lib
..../test
..../tmp

app : This organizes your application components. It's got subdirectories that hold the view (views and helpers), controller (controllers), and the backend business logic (models).

app/controllers: The controllers subdirectory is where Rails looks to find controller classes. A controller handles a web request from the user.

app/helpers: The helpers subdirectory holds any helper classes used to assist the model, view, and controller classes. This helps to keep the model, view, and controller code small, focused, and uncluttered.

app/views: The views subdirectory holds the display templates to fill in with data from our application, convert to HTML, and return to the user's browser.

app/views/layouts: Holds the template files for layouts to be used with views. This models the common header/footer method of wrapping views. In your views, define a layout using the <tt>layout :default</tt> and create a file named default.rhtml. Inside default.rhtml, call <% yield %> to render the view using this layout.

doc: Ruby has a framework, called RubyDoc, that can automatically generate documentation for code you create. You can assist RubyDoc with comments in your code. This directory holds all theR ubyDoc-generated Rails and application documentation.

lib: You'll put libraries here, unless they explicitly belong elsewhere (such as vendor libraries).

log: Error logs go here. Rails creates scripts that help you manage various error logs. You'll find separate logs for the server (server.log) and each Rails environment (development.log, test.log, and production.log).

test: The tests you write and those Rails creates for you all go here. You'll see a subdirectory for mocks (mocks), unit tests (unit), fixtures (fixtures), and functional tests (functional).

tmp: Rails uses this directory to hold temporary files for intermediate processing.
