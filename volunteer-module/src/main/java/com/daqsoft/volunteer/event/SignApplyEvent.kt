package com.daqsoft.volunteer.event

import com.daqsoft.volunteer.bean.VolunteerTeamBean
import com.daqsoft.volunteer.bean.VolunteerTeamDetailBean

/**
 *@package:com.daqsoft.volunteer.event
 *@date:2020/6/19 16:25
 *@author: caihj
 *@des:TODO
 **/
class SignApplyEvent(val volunteerTeam: VolunteerTeamDetailBean){
    val volunteerTeamDetail = volunteerTeam
}