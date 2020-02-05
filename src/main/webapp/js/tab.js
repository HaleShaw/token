$(document).ready(function () {

  (function ($) {
    $('.tab ul.tabs').find('> li:eq(0)').addClass('active');

    $('.tab ul.tabs li a').click(function (g) {
      var tab = $(this).closest('.tab'),
        index = $(this).closest('li').index();

      tab.find('ul.tabs > li').removeClass('active');
      $(this).closest('li').addClass('active');

      tab.find('.tab_content').find('div.tabs_item').not('div.tabs_item:eq(' + index + ')').hide();
      tab.find('.tab_content').find('div.tabs_item:eq(' + index + ')').fadeIn(600);

      g.preventDefault();
    });
  })(jQuery);
});
