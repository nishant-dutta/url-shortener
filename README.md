# Goal

The goal of this project would be to build a url shortener using Spring Boot, Hibernate, H2 and migrate the project to spring cloud at a later point.

Before moving on let’s get some basic idea of redirection using Java.

## Solutions for redirecting requests using Java

### Redirect with RedirectView
    @Controller
    @RequestMapping("/")
    public class RedirectController {

    @GetMapping("/redirectWithRedirectView")
        public RedirectView redirectWithUsingRedirectView(
            RedirectAttributes attributes) {
                attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
                attributes.addAttribute("attribute", "redirectWithRedirectView");
                return new RedirectView("redirectedUrl");
        }
    }
Behind the scenes, **RedirectView** will trigger an **HttpServletResponse.sendRedirect()**, 
which will perform the actual redirect.
Notice here how we’re injecting the redirect attributes into the method. 
The framework will do the heavy lifting and allow us to interact with these attributes.
We’re adding the model attribute **"attribute"**, which will be exposed as an **HTTP query parameter**. 
The model must contain only objects — 
Generally **Strings** or **Objects** that can be converted to Strings.
We can also set a temporary redirect with 301 status which are cached by browsers 
**(where is it stored on browser and how can we see them?)** in the following manner:

    redirectView.setStatusCode(HttpStatusCode.valueOf(301));

### Redirect with Prefix "redirect:"
The previous approach — using **RedirectView** — is not suitable for all scenarios for a few reasons.

* we’re coupled to the Spring API because we’re using the RedirectView directly in our code.
* we need to know from the start, when implementing that controller operation, that the result will always be a redirect, which may not always be the case.

A better option is using the prefix "**redirect:**". The redirect view name is injected into the controller like any other logical view name. 
The controller is not even aware that redirection is happening.

    @Controller
    @RequestMapping("/")
    public class RedirectController {
    
        @GetMapping("/redirectWithRedirectPrefix")
        public ModelAndView redirectWithUsingRedirectPrefix(ModelMap model) {
            model.addAttribute("attribute", "redirectWithRedirectPrefix");
            return new ModelAndView("redirect:/redirectedUrl", model);
        }
    }

When a view name is returned with the prefix **redirect:**, 
the UrlBasedViewResolver (and all its subclasses) will recognize this as a special indication that 
a redirect needs to happen. 
The rest of the view name will be used as the redirect URL.
A quick but important note is that when we use this logical view name here — 
**redirect:/redirectedUrl** — we’re doing a redirect relative to the current Servlet context.
We can use a name such as a redirect:http://localhost:8080/spring-redirect-and-forward/redirectedUrl if we need to redirect to an absolute URL.

### Redirect with Prefix "forward:"

let’s go over a quick, high-level overview of the semantics of forward vs redirect:

* **redirect** will respond with a **302** and the new URL in the **Location** header. 
The browser/client will then make another request to the new URL.
* **forward:** happens entirely on a server side. 
The **Servlet container** forwards the same request to the target URL and returns the response somewhat acting like a proxy
and **the URL doesn’t change in the browser**.

    @Controller
    @RequestMapping("/")
    public class RedirectController {
    
        @GetMapping("/forwardWithForwardPrefix")
        public ModelAndView redirectWithUsingForwardPrefix(ModelMap model) {
            model.addAttribute("attribute", "forwardWithForwardPrefix");
            return new ModelAndView("forward:/redirectedUrl", model);
        }
    }

Same as **redirect:**, the **forward:** prefix will be resolved by **UrlBasedViewResolver** 
and its subclasses. Internally, this will create an **InternalResourceView**, which does a 
**RequestDispatcher.forward()** to the new view.

**NOTE**: During the POC external URLs with forward: attribute were giving errors but the internal 
URLs went through successfully bringing response to the browser without changing the URL like we have 
in case of redirect: attributes. Since, the URL remains the same, when the page is reloaded, 
it asks for form submission confirmation in case of using forms.

Now - let’s consider a scenario where we would want to send some parameters across to another 
**RequestMapping** with a **forward:** prefix.
In that case, we can use an **HttpServletRequest** to pass in parameters between calls.

Here’s a method **forwardWithParams()** that needs to send param1 and param2 to another mapping **forwardedWithParams**:

    @RequestMapping(value="/forwardWithParams", method = RequestMethod.GET)
    public ModelAndView forwardWithParams(HttpServletRequest request) {
        request.setAttribute("param1", "one");
        request.setAttribute("param2", "two");
        return new ModelAndView("forward:/forwardedWithParams");
    }

## Passing attributes with RedirectAttributes
let’s look closer at passing attributes in a redirect, making full use of the framework with **RedirectAttributes**:

    @GetMapping("/redirectWithRedirectAttributes")
    public RedirectView redirectWithRedirectAttributes(RedirectAttributes attributes) {
        attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectAttributes");
        attributes.addAttribute("attribute", "redirectWithRedirectAttributes");
        return new RedirectView("redirectedUrl");
    }

As we saw before, we can inject the attributes object in the method directly, 
which makes this mechanism very easy to use.

Notice also that we are **adding a flash attribute as well**. 
This is an attribute that won’t make it into the URL.
With this kind of attribute, we can later access the flash attribute using 
**@ModelAttribute(“flashAttribute”)** only in the method that is the **final target of the redirect**
in the following manner:

    @GetMapping("/redirectedUrl")
    public ModelAndView redirection(ModelMap model, @ModelAttribute("flashAttribute") Object flashAttribute) {
        model.addAttribute("redirectionAttribute", flashAttribute);
        return new ModelAndView("redirection", model);
    }

That way, using RedirectAttributes instead of a ModelMap gives us the ability only to share 
**some attributes between the two methods** that are involved in the redirect operation.

## Redirecting an HTTP POST Request
For use cases like bank payments, we might need to redirect an HTTP POST request. 
Depending on the HTTP status code returned, POST request can be redirected to an HTTP GET or POST.
As per HTTP 1.1 protocol [reference](https://tools.ietf.org/html/rfc7238),

* status codes **301 (Moved Permanently)** and **302 (Found)** allow the request method to be 
changed from POST to GET. **This won’t be suitable for our usecase as the POST requests would 
automatically get converted to GET requests**
* The specification also defines the corresponding **307 (Temporary Redirect)** and 
**308 (Permanent Redirect)** status codes that don’t allow the request method to be changed from 
POST to GET.

**Observation**: **POST Requests were not cached. Debug more on this!**

Let’s look at the code for redirecting a post request to another post request:

    @PostMapping("/redirectPostToPost")
    public ModelAndView redirectPostToPost(HttpServletRequest request) {
        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:/redirectedPostToPost");
    }
