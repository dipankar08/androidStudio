package in.co.dipankar.fragmentnavigation.Page;

public enum PageLocation {
  FIRST_PAGE,
  SECOND_PAGE,
  THIRD_PAGE,
  FORTH_PAGE;

  public static PageLocation getLocation(String location) {
    return FIRST_PAGE;
  }
}
