# PayUMoneyDemo
This is a sample project which explains the pay u money integration to the android application

The Hash key was generated here itself in this demo but i suggest you to fetch it from server for security purposes.

If you are opening the payment gateway from fragment you wont get the result back to the fragment, but you can catch that in the fragments activity file using onActivityResult method.. 

Sample Code for that : 

' public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    Fragment fragment = (Fragment) getChildFragmentManager().findFragmentByTag(childTag);
    if (fragment != null) {
        fragment.onActivityResult(requestCode, resultCode, intent);
    }
} '
