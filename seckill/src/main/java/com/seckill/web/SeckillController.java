package com.seckill.web;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.dto.SeckillResult;
import com.seckill.entity.Seckill;
import com.seckill.enums.SeckillEnum;
import com.seckill.exception.RepeatSeckillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by tyd on 2017-8-31.
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {

    Logger logger = LoggerFactory.getLogger(SeckillController.class);

    @Autowired
    private SeckillService seckillService;

    /**
     * 获取秒杀列表接口
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        List<Seckill> seckillList = seckillService.getSeckillList();

        model.addAttribute("seckillList", seckillList);

        return "/seckillList";  //WEB-INF/jsp/seckillList.jsp
    }

    /**
     * 获取秒杀详情
     *
     * @param seckillId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {

        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getSeckillById(seckillId);

        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "/seckillDetail";
    }

    /**
     * 获取秒杀URL接口
     *
     * @param seckillId
     * @return
     */
    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result;

        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }

        return result;
    }

    /**
     * 执行秒杀接口
     *
     * @param seckillId
     * @param md5
     * @param userPhone
     * @return
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execution(@PathVariable("seckillId") Long seckillId,
                                                     @PathVariable("md5") String md5,
                                                     @RequestParam(value = "userPhone", required = false) Long userPhone) {

        SeckillResult<SeckillExecution> result;

        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, md5);
            result = new SeckillResult<SeckillExecution>(true, execution);
        } catch (SeckillCloseException e) {
            result = new SeckillResult<SeckillExecution>(true, new SeckillExecution(seckillId, SeckillEnum.END));
        } catch (RepeatSeckillException e) {
            result = new SeckillResult<SeckillExecution>(true, new SeckillExecution(seckillId, SeckillEnum.REPEAT_SECKILL));
        } catch (SeckillException e) {
            result = new SeckillResult<SeckillExecution>(true, new SeckillExecution(seckillId, SeckillEnum.INNER_ERROR));
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = new SeckillResult<SeckillExecution>(false, e.getMessage());
        }

        return result;
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    @RequestMapping("/getTime")
    @ResponseBody
    public SeckillResult<Long> getTime() {
        SeckillResult<Long> seckillResult;
        Long now = System.currentTimeMillis();
        seckillResult = new SeckillResult<Long>(true, now);
        return seckillResult;
    }

}
