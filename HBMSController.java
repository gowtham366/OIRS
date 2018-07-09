package com.capgemini.hbms.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.jms.Session;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.capgemini.hbms.bean.HBMSBookingBean;
import com.capgemini.hbms.bean.HBMSGuestBean;
import com.capgemini.hbms.bean.HBMSHotelBean;
import com.capgemini.hbms.bean.HBMSRoomBean;
import com.capgemini.hbms.bean.HBMSUserBean;
import com.capgemini.hbms.exception.HBMSBookingException;
import com.capgemini.hbms.exception.HBMSException;
import com.capgemini.hbms.exception.HBMSUserException;
import com.capgemini.hbms.service.HBMSServiceImpl;
import com.capgemini.hbms.service.IHBMSService;

/**
 * Servlet implementation class HBMSController
 */
@WebServlet("/HBMSController")
public class HBMSController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HBMSController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String operation=request.getParameter("action");
		PrintWriter write=response.getWriter();
		IHBMSService service=new HBMSServiceImpl();
		try
		{
			if(operation.equals("login"))
			{
				String username=request.getParameter("username");
				String password=request.getParameter("password");
				if(username.equals("admin") && username.equals("admin"))
				{
					RequestDispatcher rd=request.getRequestDispatcher("/AdminMain.jsp");
					rd.include(request, response);
					write.print("<br><br><br><center><h3>Welcome Admin</h3></center>");
				}
				else if(service.isValidLoginDetails(username,password))
				{
					String userid=service.getUserId(username,password);
					HttpSession session=request.getSession();
					service.checkAvaialbilityStatus();
					session.setAttribute("userid", userid);
					session.setAttribute("username", username);
					RequestDispatcher rd=request.getRequestDispatcher("/UserMain.jsp");
					rd.include(request, response);
				}
				else
				{
					RequestDispatcher rd=request.getRequestDispatcher("/StartPage.jsp");
					rd.include(request, response);
					write.print("<font color=red>invalid login details</font>");
				}
			}
			else if(operation.equals("SeeHotels"))
			{
				HttpSession session=request.getSession(false);
				if(session!=null)
				{
					ArrayList<HBMSHotelBean> hotelList=service.getHotelList();
					request.setAttribute("hotellist", hotelList);
					RequestDispatcher rd=request.getRequestDispatcher("/HotelUser.jsp");
					rd.forward(request, response);
				}
				else
				{
					RequestDispatcher rd=request.getRequestDispatcher("/StartPage.jsp");
					rd.include(request, response);
					write.print("<font color=red>please login to continue</font>");
				}
			}
			else if(operation.equals("SeebookingStatus"))
			{
				HttpSession session=request.getSession(false);
				if(session!=null)
				{
					RequestDispatcher rd=request.getRequestDispatcher("/SearchStatus.jsp");
					rd.include(request, response);
				}
				else
				{
					RequestDispatcher rd=request.getRequestDispatcher("/StartPage.jsp");
					rd.include(request, response);
					write.print("<font color=red>please login to continue</font>");
				}
			}
			else if(operation.equals("bookingdetails"))
			{
				HttpSession session=request.getSession(false);
				if(session!=null)
				{
					String bookingId=request.getParameter("bookingid");
					HBMSBookingBean booking=service.getBookingDetails(bookingId);
					if(booking==null)
					{
						RequestDispatcher rd=request.getRequestDispatcher("/SearchStatus.jsp");
						rd.include(request, response);
						write.print("please enter valid booking id");
					}
					else
					{
						request.setAttribute("booking", booking);
						RequestDispatcher rd=request.getRequestDispatcher("/BookingInfo.jsp");
						rd.include(request, response);
					}
				}
				else
				{
					RequestDispatcher rd=request.getRequestDispatcher("/StartPage.jsp");
					rd.include(request, response);
					write.print("<font color=red>please login to continue</font>");
				}
			}
			else if(operation.equals("register"))
			{
				HttpSession session=request.getSession(false);
				if(session!=null)
				{
					HBMSUserBean userBean=new HBMSUserBean();
					userBean.setPassword(request.getParameter("password"));
					String userName=request.getParameter("username");
					userName=userName.toLowerCase();
					userBean.setUserName(userName);
					if(service.isValidUserName(userBean.getUserName()))
					{
						
					}
					else
					{
						throw new HBMSUserException("username already exists");
					}
					userBean.setMobileNo(request.getParameter("mobilenum"));
					userBean.setPhone(request.getParameter("phonenum"));
					userBean.setAddress(request.getParameter("address"));
					userBean.setEmail(request.getParameter("email"));
					userBean.setRole("user");
					if(service.registerUser(userBean))
					{
						RequestDispatcher rd=request.getRequestDispatcher("/StartPage.jsp");
						rd.include(request, response);
						write.print("<font color=blue>registered sucesfully,login to continue</font>");
					}
					else
					{
						RequestDispatcher rd=request.getRequestDispatcher("/Register.jsp");
						rd.include(request, response);
						write.print("<font color=red>registration unsucessful,please register again</font>");
					}
				}
				else
				{
					RequestDispatcher rd=request.getRequestDispatcher("/StartPage.jsp");
					rd.include(request, response);
					write.print("<font color=red>please login to continue</font>");
				}
			}
			else if(operation.equals("roomDetails"))
			{
				HttpSession session=request.getSession(false);
				if(session!=null)
				{
					String id=request.getParameter("roomid");
					session.setAttribute("roomid", id);
					RequestDispatcher rd=request.getRequestDispatcher("/BookingUser.jsp");
					rd.include(request, response);
				}
				else
				{
					RequestDispatcher rd=request.getRequestDispatcher("/StartPage.jsp");
					rd.include(request, response);
					write.print("<font color=red>please login to continue</font>");
				}
			}
			else if(operation.equals("displayRooms"))
			{
				HttpSession session=request.getSession(false);
				if(session!=null)
				{
					String id=request.getParameter("hotelid");
					ArrayList<HBMSRoomBean> roomList=service.getRoomList(id);
					request.setAttribute("roomlist", roomList);
					RequestDispatcher rd=request.getRequestDispatcher("/RoomUser.jsp");
					rd.include(request, response);
				}
				else
				{
					RequestDispatcher rd=request.getRequestDispatcher("/StartPage.jsp");
					rd.include(request, response);
					write.print("<font color=red>please login to continue</font>");
				}
			}
			else if(operation.equals("addbooking"))
			{
				HttpSession session=request.getSession(false);
				if(session!=null)
				{
					HBMSBookingBean booking=new HBMSBookingBean();
					String d1=request.getParameter("bookedfromdate");
					String d2=request.getParameter("bookedtodate");
					String expectedPattern = "yyyy-MM-dd";
					SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);
					Date bookedfrom = null,bookedto = null;
					Date d3=new Date();
					int diff;
					try
					{
						bookedfrom=formatter.parse(d1);
						
					}
					catch(ParseException e)
					{
						throw new HBMSBookingException("enter valid from date");
					}
					diff=(int) ((bookedfrom.getTime()-d3.getTime()))/(1000*60*60*24);
					System.out.println(diff);
					if(diff<0)
					{
						throw new HBMSBookingException("from date cant be less than today");
					}
					try
					{
						bookedto=formatter.parse(d2);
					}
					catch(ParseException e)
					{
						throw new HBMSBookingException("enter valid to date");
					}
					diff=(int) ((bookedto.getTime()-bookedfrom.getTime()) / (1000 * 60 * 60 * 24));
					if(diff<0)
					{
						throw new HBMSBookingException("to date cant be less than from date");
					}
					else if(diff>10)
					{
						throw new HBMSBookingException("you can book for a maximum period of 10 days");
					}
					int noofadults=Integer.parseInt(request.getParameter("noofadults"));
					int noofchildren=Integer.parseInt(request.getParameter("noofchildren"));
					if((noofadults+noofchildren)>4)
					{
						throw new HBMSBookingException("A room can accomdate a maximum of 4 people");
					}
					String roomid=(String) session.getAttribute("roomid");
					float amount=service.getRoomAmount(roomid);
					if(diff>0)
					{
						amount=amount*diff;
					}
					String userid=(String)session.getAttribute("userid");
					booking.setUserId(userid);
					booking.setRoomId(roomid);
					booking.setBokkedFrom(bookedfrom);
					booking.setBookedTo(bookedto);
					booking.setNoOfAdults(noofadults);
					booking.setNoOfChildren(noofchildren);
					booking.setAmount(amount);
					String bookingId=service.addBookingDetails(booking);
					booking.setBookingId(bookingId);
					request.setAttribute("booking", booking);
					service.changeRoomStatus(roomid);
					RequestDispatcher rd=request.getRequestDispatcher("/BookingInfo.jsp");
					rd.include(request, response);					
				}
				else
				{
					RequestDispatcher rd=request.getRequestDispatcher("/StartPage.jsp");
					rd.include(request, response);
					write.print("<font color=red>please login to continue</font>");
				}
			}
			else if(operation.equals("logout"))
			{
				HttpSession session=request.getSession(false);
				session.invalidate();
				RequestDispatcher rd=request.getRequestDispatcher("/StartPage.jsp");
				rd.include(request, response);
				write.print("logged out sucessfully");
			}
			else if(operation.equals("AddHotel"))
			{
				HBMSHotelBean bean=new HBMSHotelBean();
				bean.setCity(request.getParameter("city"));
				bean.setHotelName(request.getParameter("hotelName"));
				bean.setAddress(request.getParameter("address"));
				bean.setDescription(request.getParameter("description"));
				float rate=Float.parseFloat(request.getParameter("avgRentPerNight"));
				bean.setAvgRatePerNight(rate);
				bean.setPhoneNo1(request.getParameter("phoneNo1"));
				bean.setPhoneNo2(request.getParameter("phoneNo2"));
				bean.setRating(request.getParameter("rating"));
				bean.setEmail(request.getParameter("email"));
				bean.setFax(request.getParameter("fax"));
				bean.setHotelPhoto(request.getParameter("image"));
				String hotelId=service.addHotelDetails(bean);
				RequestDispatcher rd=request.getRequestDispatcher("/AdminMain.jsp");
				rd.include(request, response);
				write.print("<br><br><br><center><h3>Hotel Added Sucessfully with id:"+hotelId+"</h3></center>");
			}
			else if(operation.equals("AddRoom"))
			{
				HBMSRoomBean bean=new HBMSRoomBean();
				bean.setHotelId(request.getParameter("hotelId"));
				if(service.isValidHotelId(bean.getHotelId()))
				{
					bean.setRoomNo(request.getParameter("roomNo"));
					bean.setRoomType(request.getParameter("roomType"));
					bean.setPerNightRate(Float.parseFloat(request.getParameter("avgRentPerNight")));
					bean.setAvailability(request.getParameter("Availability"));
					String image=request.getParameter("roomImage");
					String arr[]=image.split("\\");
					
					bean.setRoomPhoto(image);
					String roomId=service.addRoomDetails(bean);
					RequestDispatcher rd=request.getRequestDispatcher("/AdminMain.jsp");
					rd.include(request, response);
					write.print("<br><br><br><center><h3>Room Added Sucessfully with id:"+roomId+"</h3></center>");
				}
				else
				{
					RequestDispatcher rd=request.getRequestDispatcher("/AdminMain.jsp");
					rd.include(request, response);
					write.print("<center><font color=red>invalid hotel id</font></center>");
				}
			}
			else if(operation.equals("hotelList"))
			{
				ArrayList<HBMSHotelBean> hotelList=service.getHotelList();
				request.setAttribute("adminhotellist", hotelList);
				RequestDispatcher rd=request.getRequestDispatcher("/HotelList.jsp");
				rd.forward(request, response);
			}
			else if(operation.equals("bookingOfHotel"))
			{
				String hotelID=request.getParameter("bookhotelid");
				if(service.isValidHotelId(hotelID))
				{
					ArrayList<HBMSBookingBean> hotels=service.getBookingsOfHotel(hotelID);
					request.setAttribute("bookingHotelList",hotels );
					RequestDispatcher rd=request.getRequestDispatcher("/BookingOfHotel.jsp");
					rd.forward(request, response);
				}
				else
				{
					RequestDispatcher rd=request.getRequestDispatcher("/BookingOfHotel.jsp");
					rd.include(request, response);
					write.print("<font color=red>enter valid hotel id</font>");
				}
			}
			else if(operation.equals("deleteHotel"))
			{
				String hotelID=request.getParameter("deletehotelid");
				if(service.isValidHotelId(hotelID))
				{
					if(service.deleteHotel(hotelID))
					{
						RequestDispatcher rd=request.getRequestDispatcher("/AdminMain.jsp");
						rd.include(request, response);
						write.print("<br><br><br><center><h3>Hotel deleted Successfully</h3></center>");
					}
					else
					{
						RequestDispatcher rd=request.getRequestDispatcher("/DeleteHotel.jsp");
						rd.include(request, response);
						write.print("<br><br><br><center><h3>Sorry the hotel has some active bookings</h3></center>");
					}
				}
				else
				{
					RequestDispatcher rd=request.getRequestDispatcher("/DeleteHotel.jsp");
					rd.include(request, response);
					write.print("<font color=red>enter valid hotel id</font>");
				}
			}
			else if(operation.equals("deleteRoom"))
			{
				String roomID=request.getParameter("deleteroomid");
				if(service.isValidRoomId(roomID))
				{
					if(service.deleteRoom(roomID))
					{
						RequestDispatcher rd=request.getRequestDispatcher("/AdminMain.jsp");
						rd.include(request, response);
						write.print("<br><br><br><center><h3>Room deleted Successfully</h3></center>");
					}
					else
					{
						RequestDispatcher rd=request.getRequestDispatcher("/DeleteRoom.jsp");
						rd.include(request, response);
						write.print("<br><br><br><center><h3>The room is currently booked!!!Can't be deleted</h3></center>");
					}
				}
				else
				{
					RequestDispatcher rd=request.getRequestDispatcher("/DeleteRoom.jsp");
					rd.include(request, response);
					write.print("<font color=red>enter valid Room id</font>");
				}
			}
			else if(operation.equals("guestListOfHotel"))
			{
				String hotelID=request.getParameter("bookhotelid");
				if(service.isValidHotelId(hotelID))
				{
					ArrayList<HBMSGuestBean> hotels=service.getGuestListOfHotel(hotelID);
					request.setAttribute("guestListOfHotel",hotels );
					RequestDispatcher rd=request.getRequestDispatcher("/GuestListOfHotel.jsp");
					rd.forward(request, response);
				}
				else
				{
					RequestDispatcher rd=request.getRequestDispatcher("/GuestListOfHotel.jsp");
					rd.include(request, response);
					write.print("<font color=red>enter valid hotel id</font>");
				}
			}
			else if(operation.equals("bookingOfDate"))
			{
				String date=request.getParameter("bookingDate");
				String expectedPattern = "MM/dd/yyyy";
				Date date1 = null;
			    SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);
			    try
			    {
			    date1=formatter.parse(date);
			    }
			    catch(ParseException e)
			    {
			    	RequestDispatcher rd=request.getRequestDispatcher("/BookingOfDate.jsp");
					rd.include(request, response);
					write.print("<font color=red>enter valid date</font>");
			    }
			    ArrayList<HBMSBookingBean> bookingList=service.getBookingsOfSpecifiedDate(date1);
			    request.setAttribute("bookingList", bookingList);
			    RequestDispatcher rd=request.getRequestDispatcher("/BookingOfDate.jsp");
				rd.include(request, response);
			}
		}
		catch(HBMSException e)
		{
			write.print(e.getMessage());
		}
		catch(HBMSUserException e)
		{
			RequestDispatcher rd=request.getRequestDispatcher("/Register.jsp");
			rd.include(request, response);
			write.print("<font color=red><center>"+e+"</center></font>");
		}
		catch(HBMSBookingException e)
		{
			RequestDispatcher rd=request.getRequestDispatcher("/BookingUser.jsp");
			rd.include(request, response);
			write.print("<font color=red><center>"+e+"</center></font>");
		}
	}

}