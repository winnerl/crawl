package com.fun.crawl.security;

import com.fun.crawl.model.vo.SysUserVo;
import com.fun.crawl.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    private SysUserService sysUserService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SysUserVo sysUserVo = sysUserService.loadUserByUsername(username);
        if (sysUserVo==null){
            return null;
        }


        return new UserDetailsImpl(sysUserVo);
    }
}
